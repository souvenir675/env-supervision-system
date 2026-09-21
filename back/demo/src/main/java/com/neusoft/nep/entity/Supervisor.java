package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 公众监督员实体类
 * 对应基表：supervisor
 */
@Data
@TableName("supervisor")
public class Supervisor {

    /**
     * 手机号码 主键
     */
    @TableId
    private String telId;

    /**
     * 登录密码 长度不少于6位
     */
    @TableField("password")
    private String password;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 出生日期 格式：YYYY-MM-DD
     */
    @TableField("birthday")
    private String birthday;

    /**
     * 性别 1-男 0-女
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
}