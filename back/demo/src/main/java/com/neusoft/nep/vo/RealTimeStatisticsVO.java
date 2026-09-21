package com.neusoft.nep.vo;

import lombok.Data;

/**
 * 实时统计视图对象
 */
@Data
public class RealTimeStatisticsVO {

    private Integer totalCount;        // AQI检测累计数量
    private Integer goodCount;         // 结果良好累计数量（AQI <= 2）
    private Integer exceedCount;       // 结果超标累计数量（AQI >= 3）
}