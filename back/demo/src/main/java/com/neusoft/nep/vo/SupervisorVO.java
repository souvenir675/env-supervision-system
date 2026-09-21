package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 公众监督员视图对象
 */
@Data
public class SupervisorVO {

    private String telId;
    private String realName;
    private String birthday;
    private Integer sex;
    private String sexDesc;
    private String remarks;
}