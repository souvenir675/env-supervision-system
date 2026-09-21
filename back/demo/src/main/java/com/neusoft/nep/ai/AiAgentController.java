package com.neusoft.nep.ai;

import com.neusoft.nep.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/agent")
public class AiAgentController {

    @Autowired
    private AiAgentService aiAgentService;

    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> messages =
                (List<Map<String, Object>>) request.get("messages");
        String userType = (String) request.getOrDefault("userType", "supervisor");
        String userId = (String) request.get("userId");     // ⭐ 接收 userId

        Map<String, Object> result = aiAgentService.chat(messages, userType, userId);
        return Result.success(result);
    }
}