package com.neusoft.nep.service;

import com.neusoft.nep.entity.GridProvince;
import com.neusoft.nep.entity.GridCity;
import com.neusoft.nep.vo.GridProvinceVO;
import com.neusoft.nep.vo.GridCityVO;

import java.util.List;

/**
 * 网格区域服务接口
 */
public interface GridAreaService {

    /**
     * 查询所有省份
     */
    List<GridProvince> findAllProvinces();

    /**
     * 根据省份ID查询
     */
    GridProvince findProvinceById(Integer provinceId);

    /**
     * 根据省份ID查询城市列表
     */
    List<GridCity> findCitiesByProvinceId(Integer provinceId);

    /**
     * 查询所有城市
     */
    List<GridCity> findAllCities();

    /**
     * 转换为省份视图对象
     */
    GridProvinceVO convertProvinceToVO(GridProvince province);

    /**
     * 转换为城市视图对象
     */
    GridCityVO convertCityToVO(GridCity city);
}