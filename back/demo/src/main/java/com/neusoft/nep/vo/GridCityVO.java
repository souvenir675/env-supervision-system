package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 网格城市视图对象
 */
@Data
public class GridCityVO {

    private Integer cityId;
    private String cityName;
    private Integer provinceId;
    private String provinceName;
    private Integer covered;
    private String coveredDesc;
    private String remarks;
}