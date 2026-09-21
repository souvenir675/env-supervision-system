package com.neusoft.nep.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        // 1. 组装请求体：遵循 OpenAI 兼容格式
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", request.get("messages")); // 前端传来的对话历史
        body.put("stream", false);                      // 简化处理，先不使用流式

        // 2. 设置请求头，携带 API Key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 3. 发起请求，代理到 DeepSeek
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = baseUrl + "/chat/completions";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        return ResponseEntity.ok(response.getBody());
    }
}