package com.neusoft.nep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;



/**
 * 公众监督员注册请求DTO
 */
@Data
public class SupervisorRegisterDTO {

    /**
     * 手机号码
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String telId;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 20, message = "真实姓名不能超过20字")
    private String realName;

    /**
     * 出生日期 格式：YYYY-MM-DD
     */
    @NotBlank(message = "出生日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "出生日期格式不正确，应为YYYY-MM-DD")
    private String birthday;

    /**
     * 性别 1-男 0-女
     */
    @NotNull(message = "性别不能为空")
    @Range(min = 0, max = 1, message = "性别值无效")
    private Integer sex;
}