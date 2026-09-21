package com.neusoft.nep.service.impl;

import com.neusoft.nep.entity.GridProvince;
import com.neusoft.nep.entity.GridCity;
import com.neusoft.nep.mapper.GridProvinceMapper;
import com.neusoft.nep.mapper.GridCityMapper;
import com.neusoft.nep.service.GridAreaService;
import com.neusoft.nep.vo.GridProvinceVO;
import com.neusoft.nep.vo.GridCityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 网格区域服务实现类
 */
@Service
public class GridAreaServiceImpl implements GridAreaService {

    @Autowired
    private GridProvinceMapper gridProvinceMapper;

    @Autowired
    private GridCityMapper gridCityMapper;

    @Override
    public List<GridProvince> findAllProvinces() {
        return gridProvinceMapper.selectAllList();
    }

    @Override
    public GridProvince findProvinceById(Integer provinceId) {
        return gridProvinceMapper.selectById(provinceId);
    }

    @Override
    public List<GridCity> findCitiesByProvinceId(Integer provinceId) {
        return gridCityMapper.selectByProvinceId(provinceId);
    }

    @Override
    public List<GridCity> findAllCities() {
        return gridCityMapper.selectList(null);
    }

    @Override
    public GridProvinceVO convertProvinceToVO(GridProvince province) {
        if (province == null) {
            return null;
        }
        GridProvinceVO vo = new GridProvinceVO();
        BeanUtils.copyProperties(province, vo);
        vo.setCoveredDesc(province.getCovered() != null && province.getCovered() == 1 ? "已覆盖" : "未覆盖");
        return vo;
    }

    @Override
    public GridCityVO convertCityToVO(GridCity city) {
        if (city == null) {
            return null;
        }
        GridCityVO vo = new GridCityVO();
        BeanUtils.copyProperties(city, vo);

        GridProvince p = gridProvinceMapper.selectById(city.getProvinceId());
        if (p != null) {
            vo.setProvinceName(p.getProvinceName());
        }

        vo.setCoveredDesc(city.getCovered() != null && city.getCovered() == 1 ? "已覆盖" : "未覆盖");
        return vo;
    }
}