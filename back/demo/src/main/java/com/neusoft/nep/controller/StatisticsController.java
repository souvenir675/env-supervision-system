package com.neusoft.nep.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.nep.common.Result;
import com.neusoft.nep.dto.AqiDataSubmitDTO;
import com.neusoft.nep.dto.StatisticsQueryDTO;
import com.neusoft.nep.service.StatisticsService;
import com.neusoft.nep.vo.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
@Validated
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 网格员提交实测AQI数据
     */
    @PostMapping("/submit")
    public Result<Boolean> submitAqiData(@Valid @RequestBody AqiDataSubmitDTO dto) {
        try {
            boolean result = statisticsService.submitAqiData(dto);
            return Result.success(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询AQI确认数据列表
     */
    @PostMapping("/list")
    public Result<Page<StatisticsVO>> queryConfirmStatisticsList(@RequestBody StatisticsQueryDTO queryDTO) {
        Page<StatisticsVO> page = statisticsService.queryStatisticsPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 查询AQI确认数据详情
     */
    @GetMapping("/detail/{id}")
    public Result<StatisticsVO> getStatisticsDetail(@PathVariable Integer id) {
        StatisticsVO vo = statisticsService.getStatisticsDetail(id);
        if (vo == null) {
            return Result.error("数据不存在");
        }
        return Result.success(vo);
    }

    /**
     * 省分组检查统计
     */
    @PostMapping("/province")
    public Result<List<ProvinceStatisticsVO>> getProvinceStatistics(@RequestBody StatisticsQueryDTO queryDTO) {
        List<ProvinceStatisticsVO> list = statisticsService.getProvinceStatistics(queryDTO);
        return Result.success(list);
    }

    /**
     * AQI指数分布统计
     */
    @GetMapping("/distribution")
    public Result<List<AqiDistributionVO>> getAqiDistribution() {
        List<AqiDistributionVO> list = statisticsService.getAqiDistribution();
        return Result.success(list);
    }

    /**
     * AQI指数趋势统计
     */
    @GetMapping("/trend")
    public Result<List<TrendStatisticsVO>> getAqiTrend() {
        List<TrendStatisticsVO> list = statisticsService.getAqiTrend();
        return Result.success(list);
    }

    /**
     * 空气质量检测数量实时统计
     */
    @GetMapping("/realtime")
    public Result<RealTimeStatisticsVO> getRealTimeStatistics() {
        RealTimeStatisticsVO vo = statisticsService.getRealTimeStatistics();
        return Result.success(vo);
    }

    /**
     * 全国网格覆盖率统计
     */
    @GetMapping("/coverage")
    public Result<CoverageStatisticsVO> getCoverageStatistics() {
        CoverageStatisticsVO vo = statisticsService.getCoverageStatistics();
        return Result.success(vo);
    }

    /**
     * 城市分组统计（按省份）
     */
    @GetMapping("/city/{provinceId}")
    public Result<List<CityStatisticsVO>> getCityStatistics(@PathVariable Integer provinceId) {
        List<CityStatisticsVO> list = statisticsService.getCityStatisticsByProvince(provinceId);
        return Result.success(list);
    }

    /**
     * 查询某城市下的监测点
     */
    @GetMapping("/monitor-points")
    public Result<List<StatisticsVO>> getMonitorPoints(
            @RequestParam Integer provinceId,
            @RequestParam Integer cityId) {
        List<StatisticsVO> list = statisticsService.getMonitorPointsByCity(provinceId, cityId);
        return Result.success(list);
    }
}