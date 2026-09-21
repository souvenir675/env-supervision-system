package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 网格员视图对象
 */
@Data
public class GridMemberVO {

    private String gmId;
    private String gmName;
    private String gmCode;
    private Integer provinceId;
    private String provinceName;
    private Integer cityId;
    private String cityName;
    private String tel;
    private Integer state;
    private String stateDesc;
    private String remarks;
}