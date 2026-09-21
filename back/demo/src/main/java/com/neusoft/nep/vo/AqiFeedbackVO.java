package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 反馈信息视图对象
 */
@Data
public class AqiFeedbackVO {

    /**
     * 反馈信息编号
     */
    private Integer afId;

    /**
     * 监督员手机号
     */
    private String telId;

    /**
     * 监督员姓名
     */
    private String supervisorName;

    /**
     * 监督员联系电话（新增，与telId相同，便于前端展示）
     */
    private String supervisorTel;

    /**
     * 监督员性别（新增）0-女 1-男
     */
    private Integer supervisorSex;
    private String supervisorSexDesc;

    /**
     * 监督员出生日期（新增）
     */
    private String supervisorBirthday;

    /**
     * 省区域编号
     */
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市区域编号
     */
    private Integer cityId;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 反馈描述
     */
    private String information;

    /**
     * 预估AQI等级
     */
    private Integer estimatedGrade;

    /**
     * 预估AQI等级描述
     */
    private String estimatedGradeDesc;

    /**
     * 实测AQI等级（新增）
     */
    private Integer measuredGrade;
    private String measuredGradeDesc;
    private String measuredGradeColor;

    /**
     * 实测污染物数据（新增）
     */
    private Integer so2Value;
    private Integer so2Level;
    private String so2LevelDesc;
    private String so2Color;
    private Integer coValue;
    private Integer coLevel;
    private String coLevelDesc;
    private String coColor;
    private Integer spmValue;
    private Integer spmLevel;
    private String spmLevelDesc;
    private String spmColor;

    /**
     * 实测确认时间（新增）
     */
    private String confirmDate;
    private String confirmTime;

    /**
     * 反馈日期
     */
    private String afDate;

    /**
     * 反馈时间
     */
    private String afTime;

    /**
     * 指派网格员编号
     */
    private String gmId;

    /**
     * 网格员姓名
     */
    private String gridMemberName;

    /**
     * 指派日期
     */
    private String assignDate;

    /**
     * 指派时间
     */
    private String assignTime;

    /**
     * 信息状态 0-未指派，1-已指派，2-已确认
     */
    private Integer state;

    /**
     * 状态描述
     */
    private String stateDesc;
}