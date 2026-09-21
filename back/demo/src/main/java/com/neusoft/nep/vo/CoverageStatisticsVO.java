package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 网格覆盖率统计视图对象
 */
@Data
public class CoverageStatisticsVO {

    /**
     * 省份覆盖率
     */
    private Double provinceCoverage;

    /**
     * 省份已覆盖数
     */
    private Integer provinceCovered;

    /**
     * 省份总数
     */
    private Integer provinceTotal;

    /**
     * 城市覆盖率
     */
    private Double cityCoverage;

    /**
     * 城市已覆盖数
     */
    private Integer cityCovered;

    /**
     * 城市总数
     */
    private Integer cityTotal;
}