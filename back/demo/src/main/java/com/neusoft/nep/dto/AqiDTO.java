package com.neusoft.nep.dto;

import lombok.Data;

/**
 * AQI查询请求DTO
 */
@Data
public class AqiDTO {

    /**
     * AQI等级编号
     */
    private Integer aqiId;

    /**
     * 等级描述
     */
    private String aqiExplain;
}