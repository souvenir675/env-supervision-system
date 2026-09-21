package com.neusoft.nep.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 网格员指派请求DTO
 */
@Data
public class GridMemberAssignDTO {

    /**
     * 反馈信息编号
     */
    @NotNull(message = "反馈编号不能为空")
    private Integer afId;

    /**
     * 网格员编号
     */
    @NotNull(message = "网格员编号不能为空")
    private String gmId;
}