package com.neusoft.nep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * 网格员提交实测AQI数据请求DTO
 */
@Data
public class AqiDataSubmitDTO {

    /**
     * 反馈信息编号
     */
    @NotNull(message = "反馈编号不能为空")
    private Integer afId;

    /**
     * SO₂实测浓度值 (ug/m³)
     */
    @NotNull(message = "SO₂浓度值不能为空")
    private Integer so2Value;

    /**
     * CO实测浓度值 (ug/m³)
     */
    @NotNull(message = "CO浓度值不能为空")
    private Integer coValue;

    /**
     * PM2.5实测浓度值 (ug/m³)
     */
    @NotNull(message = "PM2.5浓度值不能为空")
    private Integer spmValue;

    /**
     * 网格员编号
     */
    @NotBlank(message = "网格员编号不能为空")
    private String gmId;
}