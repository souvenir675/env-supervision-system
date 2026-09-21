package com.neusoft.nep.dto;

import lombok.Data;

/**
 * 反馈查询请求DTO
 */
@Data
public class AqiFeedbackQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 监督员手机号
     */
    private String telId;

    /**
     * 省区域编号
     */
    private Integer provinceId;

    /**
     * 市区域编号
     */
    private Integer cityId;

    /**
     * 反馈状态 0-未指派，1-已指派，2-已确认
     */
    private Integer state;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 网格员编号
     */
    private String gmId;
}