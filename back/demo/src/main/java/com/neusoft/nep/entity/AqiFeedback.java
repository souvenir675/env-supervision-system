package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 公众监督反馈信息实体类
 * 对应基表：aqi_feedback
 */
@Data
@TableName("aqi_feedback")
public class AqiFeedback {

    /**
     * 反馈信息编号 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Integer afId;

    /**
     * 监督员手机号 外键参照supervisor
     */
    @TableField("tel_id")
    private String telId;

    /**
     * 省区域编号 外键参照grid_province
     */
    @TableField("province_id")
    private Integer provinceId;

    /**
     * 市区域编号 外键参照grid_city
     */
    @TableField("city_id")
    private Integer cityId;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 反馈描述
     */
    @TableField("information")
    private String information;

    /**
     * 预估AQI等级 值域：1-6
     */
    @TableField("estimated_grade")
    private Integer estimatedGrade;

    /**
     * 反馈日期 格式：YYYY-MM-DD
     */
    @TableField("af_date")
    private String afDate;

    /**
     * 反馈时间 格式：HH:MM:SS
     */
    @TableField("af_time")
    private String afTime;

    /**
     * 指派网格员编号 0表示未指派
     */
    @TableField("gm_id")
    private String gmId;

    /**
     * 指派日期 格式：YYYY-MM-DD
     */
    @TableField("assign_date")
    private String assignDate;

    /**
     * 指派时间 格式：HH:MM:SS
     */
    @TableField("assign_time")
    private String assignTime;

    /**
     * 信息状态 0-未指派，1-已指派，2-已确认
     */
    @TableField("state")
    private Integer state;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}