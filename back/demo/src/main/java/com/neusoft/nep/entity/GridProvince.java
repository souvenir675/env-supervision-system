package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 网格省份实体类
 * 对应基表：grid_province
 */
@Data
@TableName("grid_province")
public class GridProvince {

    /**
     * 省区域编号 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Integer provinceId;

    /**
     * 省区域名称
     */
    @TableField("province_name")
    private String provinceName;

    /**
     * 省区域简称
     */
    @TableField("province_abbr")
    private String provinceAbbr;

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