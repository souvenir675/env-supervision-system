package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 决策者实体类
 * 对应基表：decision_maker
 */
@Data
@TableName("decision_maker")
public class DecisionMaker {

    /**
     * 决策者编号 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Integer dmId;

    /**
     * 决策者登录编号
     */
    @TableField("dm_code")
    private String dmCode;

    /**
     * 决策者登录密码
     */
    @TableField("password")
    private String password;

    /**
     * 决策者姓名
     */
    @TableField("dm_name")
    private String dmName;

    /**
     * 角色 用于标识权限层级
     */
    @TableField("role")
    private String role;

    /**
     * 账号状态 1-启用 0-停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后登录日期 格式：YYYY-MM-DD
     */
    @TableField("last_login_date")
    private String lastLoginDate;

    /**
     * 最后登录时间 格式：HH:MM:SS
     */
    @TableField("last_login_time")
    private String lastLoginTime;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}