package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 网格省份视图对象
 */
@Data
public class GridProvinceVO {

    private Integer provinceId;
    private String provinceName;
    private String provinceAbbr;
    private Integer covered;
    private String coveredDesc;
    private String remarks;
}