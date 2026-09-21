package com.neusoft.nep.vo;

import lombok.Data;

/**
 * AQI趋势统计视图对象
 */
@Data
public class TrendStatisticsVO {

    private Integer seqNo;
    private String month;              // 月份 格式：YYYY-MM
    private Integer exceedCount;       // 超标累计数量
}