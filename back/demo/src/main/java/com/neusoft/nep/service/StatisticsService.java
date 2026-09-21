package com.neusoft.nep.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.nep.dto.AqiDataSubmitDTO;
import com.neusoft.nep.dto.StatisticsQueryDTO;
import com.neusoft.nep.vo.*;

import java.util.List;

public interface StatisticsService {

    boolean submitAqiData(AqiDataSubmitDTO dto);

    Page<StatisticsVO> queryStatisticsPage(StatisticsQueryDTO queryDTO);

    StatisticsVO getStatisticsDetail(Integer id);

    List<ProvinceStatisticsVO> getProvinceStatistics(StatisticsQueryDTO queryDTO);

    List<AqiDistributionVO> getAqiDistribution();

    List<TrendStatisticsVO> getAqiTrend();

    RealTimeStatisticsVO getRealTimeStatistics();

    CoverageStatisticsVO getCoverageStatistics();

    /**
     * 城市分组统计（按省份）
     */
    List<CityStatisticsVO> getCityStatisticsByProvince(Integer provinceId);

    /**
     * 查询某城市下的所有监测点
     */
    List<StatisticsVO> getMonitorPointsByCity(Integer provinceId, Integer cityId);
}