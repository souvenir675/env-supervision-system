package com.neusoft.nep.dto;

import lombok.Data;

/**
 * 统计数据查询DTO
 */
@Data
public class StatisticsQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /**
     * 省区域编号
     */
    private Integer provinceId;

    /**
     * 市区域编号
     */
    private Integer cityId;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * AQI等级
     */
    private Integer aqiId;
}