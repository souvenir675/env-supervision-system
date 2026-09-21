package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 省分组统计视图对象
 */
@Data
public class ProvinceStatisticsVO {

    private Integer provinceId;
    private String provinceName;
    private Integer so2ExceedCount;    // SO₂超标累计数量
    private Integer coExceedCount;     // CO超标累计数量
    private Integer spmExceedCount;    // PM2.5超标累计数量
    private Integer aqiExceedCount;    // AQI超标累计数量
    private Integer totalCount;        // 总检测数量
}