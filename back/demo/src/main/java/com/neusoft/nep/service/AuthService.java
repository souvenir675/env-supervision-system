package com.neusoft.nep.service;

import com.neusoft.nep.dto.LoginDTO;
import com.neusoft.nep.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 统一登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 验证Token
     */
    boolean validateToken(String token);

    /**
     * 从Token中获取用户信息
     */
    String getUserIdFromToken(String token);

    /**
     * 从Token中获取用户类型
     */
    String getUserTypeFromToken(String token);
}