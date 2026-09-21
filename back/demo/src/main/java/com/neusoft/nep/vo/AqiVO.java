package com.neusoft.nep.vo;

import lombok.Data;

/**
 * AQI视图对象
 */
@Data
public class AqiVO {

    /**
     * AQI等级编号
     */
    private Integer aqiId;

    /**
     * 等级汉字表述
     */
    private String chineseExplain;

    /**
     * 等级描述
     */
    private String aqiExplain;

    /**
     * 表示颜色
     */
    private String color;

    /**
     * 对健康影响
     */
    private String healthImpact;

    /**
     * 建议措施
     */
    private String takeSteps;

    /**
     * SO₂浓度范围
     */
    private String so2Range;

    /**
     * CO浓度范围
     */
    private String coRange;

    /**
     * PM2.5浓度范围
     */
    private String spmRange;
}