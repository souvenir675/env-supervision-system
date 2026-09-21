package com.neusoft.nep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;


/**
 * 网格员修改工作状态DTO
 */
@Data
public class GridMemberStateUpdateDTO {

    /**
     * 网格员编号（手机号）
     */
    @NotBlank(message = "网格员编号不能为空")
    private String gmId;

    /**
     * 工作状态 0-可工作 1-临时抽调 2-休假 3-其它
     */
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 3, message = "状态值无效")
    private Integer state;
}