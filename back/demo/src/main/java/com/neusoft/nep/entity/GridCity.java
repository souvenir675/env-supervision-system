package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 网格城市实体类
 * 对应基表：grid_city
 */
@Data
@TableName("grid_city")
public class GridCity {

    /**
     * 市区域编号 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Integer cityId;

    /**
     * 市区域名称
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 所属省区域编号 外键参照grid_province
     */
    @TableField("province_id")
    private Integer provinceId;

    /**
     * 是否已覆盖 0-未覆盖 1-已覆盖
     */
    @TableField("covered")
    private Integer covered;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}