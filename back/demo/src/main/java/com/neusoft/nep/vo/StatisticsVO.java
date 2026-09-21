package com.neusoft.nep.vo;

import lombok.Data;

/**
 * AQI统计数据视图对象
 */
@Data
public class StatisticsVO {

    private Integer id;
    private Integer afId;
    private Integer provinceId;
    private String provinceName;
    private Integer cityId;
    private String cityName;
    private String address;
    private Integer so2Value;
    private Integer so2Level;
    private String so2LevelDesc;
    private Integer coValue;
    private Integer coLevel;
    private String coLevelDesc;
    private Integer spmValue;
    private Integer spmLevel;
    private String spmLevelDesc;
    private String remarks;
    private Integer aqiId;
    private String aqiLevelDesc;
    private String aqiColor;
    private String confirmDate;
    private String confirmTime;
    private String gmId;
    private String gmName;
    private String gmTel;
    private String fdId;
    private String fdName;
    private String fdTel;
    private String information;
}