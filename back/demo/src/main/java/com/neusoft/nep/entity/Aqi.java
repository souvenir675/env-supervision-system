package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * AQI等级标准实体类
 * 对应基表：aqi
 */
@Data
@TableName("aqi")
public class Aqi {

    /**
     * AQI等级编号 1-6
     */
    @TableId(type = IdType.AUTO)
    private Integer aqiId;

    /**
     * 等级汉字表述 如"一"至"六"
     */
    @TableField("chinese_explain")
    private String chineseExplain;

    /**
     * 等级描述 如"优""良""轻度污染"等
     */
    @TableField("aqi_explain")
    private String aqiExplain;

    /**
     * 表示颜色 十六进制颜色码
     */
    @TableField("color")
    private String color;

    /**
     * 对健康影响
     */
    @TableField("health_impact")
    private String healthImpact;

    /**
     * 建议措施
     */
    @TableField("take_steps")
    private String takeSteps;

    /**
     * SO₂最小限值 (ug/m³)
     */
    @TableField("so2_min")
    private Integer so2Min;

    /**
     * SO₂最大限值 (ug/m³)
     */
    @TableField("so2_max")
    private Integer so2Max;

    /**
     * CO最小限值 (ug/m³)
     */
    @TableField("co_min")
    private Integer coMin;

    /**
     * CO最大限值 (ug/m³)
     */
    @TableField("co_max")
    private Integer coMax;

    /**
     * PM2.5最小限值 (ug/m³)
     */
    @TableField("spm_min")
    private Integer spmMin;

    /**
     * PM2.5最大限值 (ug/m³)
     */
    @TableField("spm_max")
    private Integer spmMax;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}