package com.neusoft.nep.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;



/**
 * 登录请求DTO（通用）
 */
@Data
public class LoginDTO {

    /**
     * 登录账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户类型: supervisor-公众监督员, grid-网格员, admin-管理员, decision-决策者
     */
    @NotBlank(message = "用户类型不能为空")
    private String userType;
}