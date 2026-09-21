package com.neusoft.nep.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.nep.dto.AqiDataSubmitDTO;
import com.neusoft.nep.dto.StatisticsQueryDTO;
import com.neusoft.nep.entity.*;
import com.neusoft.nep.enums.AqiGradeEnum;
import com.neusoft.nep.mapper.*;
import com.neusoft.nep.service.AqiService;
import com.neusoft.nep.service.StatisticsService;
import com.neusoft.nep.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private AqiFeedbackMapper aqiFeedbackMapper;

    @Autowired
    private AqiService aqiService;

    @Autowired
    private GridProvinceMapper gridProvinceMapper;

    @Autowired
    private GridCityMapper gridCityMapper;

    @Autowired
    private GridMemberMapper gridMemberMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitAqiData(AqiDataSubmitDTO dto) {
        // 1. 获取反馈信息
        AqiFeedback feedback = aqiFeedbackMapper.selectById(dto.getAfId());
        if (feedback == null) {
            throw new IllegalArgumentException("反馈记录不存在");
        }
        if (feedback.getState() != 1) {
            throw new IllegalStateException("该反馈未指派或已确认，不能提交实测数据");
        }

        // 2. 计算各污染物等级和AQI等级
        Integer so2Level = aqiService.calculateIAQI(dto.getSo2Value(), "so2");
        Integer coLevel = aqiService.calculateIAQI(dto.getCoValue(), "co");
        Integer spmLevel = aqiService.calculateIAQI(dto.getSpmValue(), "spm");
        Integer aqiId = aqiService.calculateAqiGrade(dto.getSo2Value(), dto.getCoValue(), dto.getSpmValue());

        // 3. 构建统计数据
        Statistics statistics = new Statistics();
        statistics.setAfId(dto.getAfId());
        statistics.setProvinceId(feedback.getProvinceId());
        statistics.setCityId(feedback.getCityId());
        statistics.setAddress(feedback.getAddress());
        statistics.setSo2Value(dto.getSo2Value());
        statistics.setSo2Level(so2Level);
        statistics.setCoValue(dto.getCoValue());
        statistics.setCoLevel(coLevel);
        statistics.setSpmValue(dto.getSpmValue());
        statistics.setSpmLevel(spmLevel);
        statistics.setAqiId(aqiId);
        statistics.setGmId(dto.getGmId());
        statistics.setFdId(feedback.getTelId());
        statistics.setInformation(feedback.getInformation());

        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();
        statistics.setConfirmDate(now.format(DATE_FORMATTER));
        statistics.setConfirmTime(time.format(TIME_FORMATTER));

        // 4. 保存统计数据
        int result = statisticsMapper.insert(statistics);
        if (result <= 0) {
            return false;
        }

        // 5. 更新反馈状态为已确认
        AqiFeedback updateFeedback = new AqiFeedback();
        updateFeedback.setAfId(dto.getAfId());
        updateFeedback.setState(2);
        aqiFeedbackMapper.updateById(updateFeedback);

        // 6. 更新省份和城市覆盖状态
        GridProvince gpUpdate = new GridProvince();
        gpUpdate.setProvinceId(feedback.getProvinceId());
        gpUpdate.setCovered(1);
        gridProvinceMapper.updateById(gpUpdate);

        GridCity gcUpdate = new GridCity();
        gcUpdate.setCityId(feedback.getCityId());
        gcUpdate.setCovered(1);
        gridCityMapper.updateById(gcUpdate);

        return true;
    }

    @Override
    public Page<StatisticsVO> queryStatisticsPage(StatisticsQueryDTO queryDTO) {
        Page<StatisticsVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return statisticsMapper.selectPageWithDetail(
                page,
                queryDTO.getProvinceId(),
                queryDTO.getCityId(),
                queryDTO.getAqiId(),
                queryDTO.getStartDate(),
                queryDTO.getEndDate()
        );
    }

    @Override
    public StatisticsVO getStatisticsDetail(Integer id) {
        return statisticsMapper.selectDetailById(id);
    }

    @Override
    public List<ProvinceStatisticsVO> getProvinceStatistics(StatisticsQueryDTO queryDTO) {
        if (queryDTO != null && StringUtils.hasText(queryDTO.getStartDate())
                && StringUtils.hasText(queryDTO.getEndDate())) {
            return statisticsMapper.selectProvinceStatisticsByDate(
                    queryDTO.getStartDate(), queryDTO.getEndDate());
        }
        return statisticsMapper.selectProvinceStatistics();
    }

    @Override
    public List<AqiDistributionVO> getAqiDistribution() {
        return statisticsMapper.selectAqiDistribution();
    }

    @Override
    public List<TrendStatisticsVO> getAqiTrend() {
        List<TrendStatisticsVO> list = statisticsMapper.selectTrendStatistics();
        // 添加序号
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setSeqNo(i + 1);
            }
        }
        return list;
    }

    @Override
    public RealTimeStatisticsVO getRealTimeStatistics() {
        List<RealTimeStatisticsVO> list = statisticsMapper.selectRealTimeStatistics();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        RealTimeStatisticsVO vo = new RealTimeStatisticsVO();
        vo.setTotalCount(0);
        vo.setGoodCount(0);
        vo.setExceedCount(0);
        return vo;
    }

    @Override
    public CoverageStatisticsVO getCoverageStatistics() {
        CoverageStatisticsVO vo = new CoverageStatisticsVO();

        int provinceTotal = gridProvinceMapper.countTotal();
        int provinceCovered = gridProvinceMapper.countCovered();
        vo.setProvinceTotal(provinceTotal);
        vo.setProvinceCovered(provinceCovered);
        vo.setProvinceCoverage(provinceTotal > 0 ?
                Math.round(provinceCovered * 100.0 / provinceTotal * 100) / 100.0 : 0.0);

        int cityTotal = gridCityMapper.countTotal();
        int cityCovered = gridCityMapper.countCovered();
        vo.setCityTotal(cityTotal);
        vo.setCityCovered(cityCovered);
        vo.setCityCoverage(cityTotal > 0 ?
                Math.round(cityCovered * 100.0 / cityTotal * 100) / 100.0 : 0.0);

        return vo;
    }

    @Override
    public List<CityStatisticsVO> getCityStatisticsByProvince(Integer provinceId) {
        if (provinceId == null) {
            return java.util.Collections.emptyList();
        }
        return statisticsMapper.selectCityStatisticsByProvince(provinceId);
    }

    @Override
    public List<StatisticsVO> getMonitorPointsByCity(Integer provinceId, Integer cityId) {
        if (provinceId == null || cityId == null) {
            return java.util.Collections.emptyList();
        }
        return statisticsMapper.selectMonitorPointsByCity(provinceId, cityId);
    }

    private StatisticsVO convertToVO(Statistics statistics) {
        if (statistics == null) {
            return null;
        }
        StatisticsVO vo = new StatisticsVO();
        BeanUtils.copyProperties(statistics, vo);

        // 设置等级描述
        AqiGradeEnum so2Grade = AqiGradeEnum.getByCode(statistics.getSo2Level());
        if (so2Grade != null) {
            vo.setSo2LevelDesc(so2Grade.getAqiExplain());
        }

        AqiGradeEnum coGrade = AqiGradeEnum.getByCode(statistics.getCoLevel());
        if (coGrade != null) {
            vo.setCoLevelDesc(coGrade.getAqiExplain());
        }

        AqiGradeEnum spmGrade = AqiGradeEnum.getByCode(statistics.getSpmLevel());
        if (spmGrade != null) {
            vo.setSpmLevelDesc(spmGrade.getAqiExplain());
        }

        AqiGradeEnum aqiGrade = AqiGradeEnum.getByCode(statistics.getAqiId());
        if (aqiGrade != null) {
            vo.setAqiLevelDesc(aqiGrade.getAqiExplain());
        }

        GridProvince p = gridProvinceMapper.selectById(statistics.getProvinceId());
        if (p != null) {
            vo.setProvinceName(p.getProvinceName());
        }

        GridCity c = gridCityMapper.selectById(statistics.getCityId());
        if (c != null) {
            vo.setCityName(c.getCityName());
        }

        GridMember m = gridMemberMapper.selectById(statistics.getGmId());
        if (m != null) {
            vo.setGmName(m.getGmName());
            vo.setGmTel(m.getTel());
        }

        if (statistics.getFdId() != null) {
            Supervisor sup = supervisorMapper.selectByTelId(statistics.getFdId());
            if (sup != null) {
                vo.setFdName(sup.getRealName());
                vo.setFdTel(sup.getTelId());
            }
        }

        return vo;
    }
}