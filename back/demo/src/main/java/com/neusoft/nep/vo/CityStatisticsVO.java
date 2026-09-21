package com.neusoft.nep.vo;

import lombok.Data;

@Data
public class CityStatisticsVO {
    private Integer cityId;
    private String cityName;
    private Integer provinceId;
    private Integer so2ExceedCount;
    private Integer coExceedCount;
    private Integer spmExceedCount;
    private Integer aqiExceedCount;
    private Integer totalCount;
}