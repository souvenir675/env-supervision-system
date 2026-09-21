package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 登录响应视图对象
 */
@Data
public class LoginVO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 令牌过期时间（毫秒）
     */
    private Long expireTime;
}