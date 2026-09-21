package com.neusoft.nep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;



/**
 * 网格员注册请求DTO（管理员操作）
 */
@Data
public class GridMemberRegisterDTO {

    /**
     * 网格员手机号（作为登录账号和主键）
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String gmId;

    /**
     * 网格员姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 20, message = "姓名不能超过20字")
    private String gmName;

    /**
     * 登录编码
     */
    @NotBlank(message = "登录编码不能为空")
    @Size(max = 20, message = "登录编码不能超过20字")
    private String gmCode;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    /**
     * 省编号
     */
    @NotNull(message = "请选择省份")
    private Integer provinceId;

    /**
     * 市编号
     */
    @NotNull(message = "请选择城市")
    private Integer cityId;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String tel;

    /**
     * 网格员状态 0-可工作 1-临时抽调 2-休假 3-其它
     */
    private Integer state = 0;

    /**
     * 备注
     */
    private String remarks;
}