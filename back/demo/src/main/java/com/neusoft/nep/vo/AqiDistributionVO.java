package com.neusoft.nep.vo;

import lombok.Data;

/**
 * AQI指数分布统计视图对象
 */
@Data
public class AqiDistributionVO {

    private Integer aqiId;
    private String aqiExplain;
    private String color;
    private Integer count;            // 该等级数量
    private Double percentage;        // 占比
}