package com.neusoft.nep.controller;

import com.neusoft.nep.common.Result;
import com.neusoft.nep.entity.GridCity;
import com.neusoft.nep.entity.GridProvince;
import com.neusoft.nep.service.GridAreaService;
import com.neusoft.nep.vo.GridCityVO;
import com.neusoft.nep.vo.GridProvinceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 网格区域控制器
 */
@RestController
@RequestMapping("/api/area")
public class GridAreaController {

    @Autowired
    private GridAreaService gridAreaService;

    /**
     * 查询所有省份
     */
    @GetMapping("/provinces")
    public Result<List<GridProvinceVO>> getProvinces() {
        List<GridProvince> provinces = gridAreaService.findAllProvinces();
        List<GridProvinceVO> voList = provinces.stream()
                .map(gridAreaService::convertProvinceToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 根据省份ID查询城市列表
     */
    @GetMapping("/cities/{provinceId}")
    public Result<List<GridCityVO>> getCitiesByProvince(@PathVariable Integer provinceId) {
        List<GridCity> cities = gridAreaService.findCitiesByProvinceId(provinceId);
        List<GridCityVO> voList = cities.stream()
                .map(gridAreaService::convertCityToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }
}