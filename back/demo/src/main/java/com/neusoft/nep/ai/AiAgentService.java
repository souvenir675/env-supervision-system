package com.neusoft.nep.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AiAgentService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${deepseek.model:deepseek-flash}")
    private String model;

    @Autowired
    private AiToolRegistry toolRegistry;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Agent 对话主流程
     */
    public Map<String, Object> chat(List<Map<String, Object>> messages, String userType,String userId) {
        // 1. 获取当前角色可用的工具
        List<Map<String, Object>> tools = toolRegistry.getTools(userType);

        // 2. 系统提示
        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", buildSystemPrompt(userType, userId));
        messages.add(0, systemMsg);

        // 3. 循环调用 AI（最多 5 轮，防止无限循环）
        List<Map<String, Object>> toolResults = new ArrayList<>();
        for (int round = 0; round < 5; round++) {
            Map<String, Object> response = callDeepSeek(messages, tools);
            if (response == null) break;

            Map<String, Object> message = (Map<String, Object>)
                    ((List<?>) response.get("choices")).get(0);
            message = (Map<String, Object>) message.get("message");

            // 加入对话历史
            messages.add(message);

            // 判断是否需要调用工具
            List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) message.get("tool_calls");
            if (toolCalls == null || toolCalls.isEmpty()) {
                // AI 直接回复，结束
                Map<String, Object> result = new HashMap<>();
                result.put("content", message.get("content") != null ? message.get("content") : "处理完成");
                result.put("toolResults", toolResults != null ? toolResults : new ArrayList<>());
                return result;   // ← 改为返回 HashMap
            }

            // 4. 执行所有工具调用
            for (Map<String, Object> toolCall : toolCalls) {
                Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
                String toolName = (String) function.get("name");
                String argsStr = (String) function.get("arguments");

                Map<String, Object> args;
                try {
                    args = objectMapper.readValue(argsStr, Map.class);
                } catch (Exception e) {
                    args = new HashMap<>();
                }

                // 执行工具
                Object result = toolRegistry.executeTool(toolName, args);
                toolResults.add(Map.of("tool", toolName, "result", result));

                // 将工具执行结果加回对话
                Map<String, Object> toolMsg = new HashMap<>();
                toolMsg.put("role", "tool");
                toolMsg.put("tool_call_id", toolCall.get("id"));
                String contentJson;
                try {
                    contentJson = objectMapper.writeValueAsString(result);
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    contentJson = "{\"error\":\"序列化失败:" + e.getMessage() + "\"}";
                }
                toolMsg.put("content", contentJson);
                messages.add(toolMsg);
            }
        }

        Map<String, Object> fallback = new HashMap<>();
        fallback.put("content", "处理超时，请重试");
        fallback.put("toolResults", toolResults != null ? toolResults : new ArrayList<>());
        return fallback;
    }

    /**
     * 调用 DeepSeek API
     */
    private Map<String, Object> callDeepSeek(List<Map<String, Object>> messages,
                                             List<Map<String, Object>> tools) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", messages);
            body.put("stream", false);
            if (tools != null && !tools.isEmpty()) {
                body.put("tools", tools);
                body.put("tool_choice", "auto");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/chat/completions", entity, Map.class);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构建系统提示
     */
    private String buildSystemPrompt(String userType, String userId) {
        String base = "你是东软环保公众监督系统的AI助手。请根据用户意图，合理调用提供的工具完成任务。" +
                "调用工具前，请先确认必要参数是否齐全；参数不全时，应先向用户询问。" +
                "工具执行后，请用自然语言总结结果给用户。";

        String userInfo = "";
        if (userId != null && !userId.isEmpty()) {
            userInfo = "\n【重要】当前登录用户的ID是：" + userId +
                    "，当调用工具需要用户身份参数（如 telId、gmId）时，直接使用这个值，不要向用户询问。";
        }

        switch (userType) {
            case "supervisor":
                return base + userInfo +"你当前服务的用户是【公众监督员】，可以帮他提交空气质量反馈、查询历史记录。";
            case "grid":
                return base + userInfo +"你当前服务的用户是【网格员】，可以帮他查询任务、提交实测AQI数据。";
            case "admin":
                return base + userInfo +
                        "你当前服务的用户是【系统管理员】，可以帮他指派网格员、查询统计数据。" +
                        "\n【指派规则 - 重要】" +
                        "\n1. 当用户说'把反馈X指派出去'或'处理反馈X'时，" +
                        "   调用 smart_assign_grid_member 工具，只传 afId，" +
                        "   系统会自动按'本地优先 → 同省就近 → 跨省'的顺序选择网格员；" +
                        "\n2. 只有用户明确指定了某个网格员（如'指派给手机号138xxx'）时，" +
                        "   才传 preferredGmId 参数；" +
                        "\n3. 工具执行后，请向用户说明指派类型（本地/异地/跨省）和网格员姓名。";
            case "decision":
                return base + userInfo +
                        "你当前服务的用户是【决策者】，主要职责是分析空气质量数据、辅助决策。" +
                        "\n【重要】日期处理规则：" +
                        "\n1. 用户提到'上个月'时，必须换算成具体日期：如 2026 年 9 月提问'上个月'指 2026-08-01 到 2026-08-31；" +
                        "\n2. 用户提到'最近三个月'时，换算成具体起止日期；" +
                        "\n3. 用户提到'本月'时，指当前月份 1 号到本月最后一天；" +
                        "\n4. 调用工具时，必须明确传入 startDate 和 endDate 参数；" +
                        "\n5. 如果用户没有明确时间范围，可以先不传日期，但在回复中说明'统计范围为全部历史数据'。" +
                        "你可以帮他：" +
                        "1) 查询各省各污染物的超标统计；" +
                        "2) 查询超标排名（哪个省最严重）；" +
                        "3) 查询月度趋势；" +
                        "4) 对比两个省份或两个时间段；" +
                        "5) 检测异常数据并给出建议。" +
                        "回答时请：先调用工具获取数据，再用简洁的语言给出结论（1-3句话），" +
                        "必要时用表格列出关键数据，但避免冗长的原始数据罗列。" +
                        "如果用户没有明确省份，默认使用全国数据。";
            default:
                return base + userInfo;
        }
    }
}