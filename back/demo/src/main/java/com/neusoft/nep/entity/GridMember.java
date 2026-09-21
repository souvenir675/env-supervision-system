package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 网格员实体类
 * 对应基表：grid_member
 */
@Data
@TableName("grid_member")
public class GridMember {

    /**
     * 网格员编号（手机号码） 主键
     */
    @TableId
    private String gmId;

    /**
     * 网格员名称
     */
    @TableField("gm_name")
    private String gmName;

    /**
     * 网格员登录编码
     */
    @TableField("gm_code")
    private String gmCode;

    /**
     * 登录密码
     */
    @TableField("password")
    private String password;

    /**
     * 省编号 外键参照grid_province
     */
    @TableField("province_id")
    private Integer provinceId;

    /**
     * 市编号 外键参照grid_city
     */
    @TableField("city_id")
    private Integer cityId;

    /**
     * 联系电话
     */
    @TableField("tel")
    private String tel;

    /**
     * 网格员状态 0-可工作 1-临时抽调 2-休假 3-其它
     */
    @TableField("state")
    private Integer state;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}