package com.neusoft.nep.util;

import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.UUID;

@Component
public class JwtUtil {

    // 简化版Token生成（仅用于测试）
    public String generateToken(String userId, String userType, String userName) {
        // 简单拼接，不用JWT库
        String raw = userId + "|" + userType + "|" + userName + "|" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }

    public boolean validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            return decoded.split("\\|").length >= 3;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            return decoded.split("\\|")[0];
        } catch (Exception e) {
            return null;
        }
    }

    public String getUserTypeFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            return decoded.split("\\|")[1];
        } catch (Exception e) {
            return null;
        }
    }

    public String getUserNameFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            return decoded.split("\\|")[2];
        } catch (Exception e) {
            return null;
        }
    }

    public Long getExpireTime() {
        return 86400000L;
    }
}