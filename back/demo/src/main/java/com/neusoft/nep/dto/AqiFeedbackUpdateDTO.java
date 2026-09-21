package com.neusoft.nep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



/**
 * 公众监督员修改反馈请求DTO
 */
@Data
public class AqiFeedbackUpdateDTO {

    /**
     * 反馈信息编号
     */
    @NotNull(message = "反馈编号不能为空")
    private Integer afId;

    /**
     * 监督员手机号（用于验证归属）
     */
    @NotBlank(message = "监督员手机号不能为空")
    private String telId;

    /**
     * 省区域编号
     */
    @NotNull(message = "省份不能为空")
    private Integer provinceId;

    /**
     * 市区域编号
     */
    @NotNull(message = "城市不能为空")
    private Integer cityId;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址不能超过200字")
    private String address;

    /**
     * 反馈描述
     */
    @NotBlank(message = "反馈描述不能为空")
    @Size(max = 400, message = "反馈描述不能超过400字")
    private String information;

    /**
     * 预估AQI等级 1-6
     */
    @NotNull(message = "预估AQI等级不能为空")
    private Integer estimatedGrade;
}