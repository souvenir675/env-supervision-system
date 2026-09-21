package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("statistics")
public class Statistics {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("af_id")
    private Integer afId;  // 新增：关联的反馈ID

    @TableField("province_id")
    private Integer provinceId;

    @TableField("city_id")
    private Integer cityId;

    @TableField("address")
    private String address;

    @TableField("so2_value")
    private Integer so2Value;

    @TableField("so2_level")
    private Integer so2Level;

    @TableField("co_value")
    private Integer coValue;

    @TableField("co_level")
    private Integer coLevel;

    @TableField("spm_value")
    private Integer spmValue;

    @TableField("spm_level")
    private Integer spmLevel;

    @TableField("aqi_id")
    private Integer aqiId;

    @TableField("confirm_date")
    private String confirmDate;

    @TableField("confirm_time")
    private String confirmTime;

    @TableField("gm_id")
    private String gmId;

    @TableField("fd_id")
    private String fdId;

    @TableField("information")
    private String information;

    @TableField("remarks")
    private String remarks;
}