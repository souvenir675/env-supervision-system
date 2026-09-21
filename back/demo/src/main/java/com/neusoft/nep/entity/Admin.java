package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统管理员实体类
 * 对应基表：admins
 */
@Data
@TableName("admins")
public class Admin {

    /**
     * 系统管理员编号 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Integer adminId;

    /**
     * 系统管理员登录编号
     */
    @TableField("admin_code")
    private String adminCode;

    /**
     * 系统管理员登录密码
     */
    @TableField("password")
    private String password;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}