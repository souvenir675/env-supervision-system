package com.neusoft.nep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neusoft.nep.entity.Aqi;
import com.neusoft.nep.mapper.AqiMapper;
import com.neusoft.nep.service.AqiService;
import com.neusoft.nep.vo.AqiVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AqiServiceImpl implements AqiService {

    @Autowired
    private AqiMapper aqiMapper;

    @Override
    public List<Aqi> findAll() {
        return aqiMapper.selectAllList();
    }

    @Override
    public Aqi findByAqiId(Integer aqiId) {
        if (aqiId == null) {
            return null;
        }
        return aqiMapper.selectById(aqiId);
    }

    @Override
    public Integer calculateAqiGrade(Integer so2Value, Integer coValue, Integer spmValue) {
        Integer so2Grade = calculateIAQI(so2Value, "so2");
        Integer coGrade = calculateIAQI(coValue, "co");
        Integer spmGrade = calculateIAQI(spmValue, "spm");
        return Math.max(Math.max(so2Grade, coGrade), spmGrade);
    }

    @Override
    public Integer calculateIAQI(Integer value, String pollutantType) {
        if (value == null || value < 0) {
            return 1;
        }

        List<Aqi> aqiList = findAll();
        if (aqiList == null || aqiList.isEmpty()) {
            return 1;
        }

        List<Aqi> sortedList = aqiList.stream()
                .sorted((a, b) -> Integer.compare(b.getAqiId(), a.getAqiId()))
                .collect(Collectors.toList());

        Integer maxPollutantValue = getMaxPollutantValue(pollutantType, sortedList);

        for (Aqi aqi : sortedList) {
            Integer min;
            Integer max;
            switch (pollutantType.toLowerCase()) {
                case "so2":
                    min = aqi.getSo2Min();
                    max = aqi.getSo2Max();
                    break;
                case "co":
                    min = aqi.getCoMin();
                    max = aqi.getCoMax();
                    break;
                case "spm":
                    min = aqi.getSpmMin();
                    max = aqi.getSpmMax();
                    break;
                default:
                    return 1;
            }

            if (min == null || max == null) {
                continue;
            }

            if (value >= min && value <= max) {
                return aqi.getAqiId();
            }
        }

        if (maxPollutantValue != null && value > maxPollutantValue) {
            return 6;
        }

        return 1;
    }

    private Integer getMaxPollutantValue(String pollutantType, List<Aqi> aqiList) {
        Integer maxVal = null;
        for (Aqi aqi : aqiList) {
            Integer val;
            switch (pollutantType.toLowerCase()) {
                case "so2":
                    val = aqi.getSo2Max();
                    break;
                case "co":
                    val = aqi.getCoMax();
                    break;
                case "spm":
                    val = aqi.getSpmMax();
                    break;
                default:
                    return null;
            }
            if (val != null && (maxVal == null || val > maxVal)) {
                maxVal = val;
            }
        }
        return maxVal;
    }

    @Override
    public AqiVO convertToVO(Aqi aqi) {
        if (aqi == null) {
            return null;
        }
        AqiVO vo = new AqiVO();
        BeanUtils.copyProperties(aqi, vo);

        // ========== 修复：组装浓度范围字符串 ==========
        // SO₂范围
        if (aqi.getSo2Min() != null && aqi.getSo2Max() != null) {
            if (aqi.getSo2Max() >= 9999) {
                vo.setSo2Range(aqi.getSo2Min() + "+");
            } else {
                vo.setSo2Range(aqi.getSo2Min() + " ~ " + aqi.getSo2Max());
            }
        } else {
            vo.setSo2Range("-");
        }

        // CO范围
        if (aqi.getCoMin() != null && aqi.getCoMax() != null) {
            if (aqi.getCoMax() >= 9999) {
                vo.setCoRange(aqi.getCoMin() + "+");
            } else {
                vo.setCoRange(aqi.getCoMin() + " ~ " + aqi.getCoMax());
            }
        } else {
            vo.setCoRange("-");
        }

        // PM2.5范围
        if (aqi.getSpmMin() != null && aqi.getSpmMax() != null) {
            if (aqi.getSpmMax() >= 9999) {
                vo.setSpmRange(aqi.getSpmMin() + "+");
            } else {
                vo.setSpmRange(aqi.getSpmMin() + " ~ " + aqi.getSpmMax());
            }
        } else {
            vo.setSpmRange("-");
        }

        return vo;
    }
}