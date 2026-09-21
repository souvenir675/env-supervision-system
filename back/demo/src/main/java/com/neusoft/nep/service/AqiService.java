package com.neusoft.nep.service;

import com.neusoft.nep.entity.Aqi;
import com.neusoft.nep.vo.AqiVO;

import java.util.List;

/**
 * AQI等级标准服务接口
 */
public interface AqiService {

    /**
     * 查询所有AQI等级
     */
    List<Aqi> findAll();

    /**
     * 根据等级编号查询
     */
    Aqi findByAqiId(Integer aqiId);

    /**
     * 根据污染物浓度值计算对应的AQI等级
     * @param so2Value SO₂浓度值 (ug/m³)
     * @param coValue CO浓度值 (ug/m³)
     * @param spmValue PM2.5浓度值 (ug/m³)
     * @return AQI等级编号
     */
    Integer calculateAqiGrade(Integer so2Value, Integer coValue, Integer spmValue);

    /**
     * 根据浓度值计算单项污染物的IAQI等级
     */
    Integer calculateIAQI(Integer value, String pollutantType);

    /**
     * 转换为视图对象
     */
    AqiVO convertToVO(Aqi aqi);
}