package com.neusoft.nep.controller;

import com.neusoft.nep.common.Result;
import com.neusoft.nep.entity.Aqi;
import com.neusoft.nep.service.AqiService;
import com.neusoft.nep.vo.AqiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aqi")
public class AqiController {

    @Autowired
    private AqiService aqiService;

    @GetMapping("/list")
    public Result<List<AqiVO>> list() {
        List<Aqi> aqiList = aqiService.findAll();
        List<AqiVO> voList = aqiList.stream()
                .map(aqiService::convertToVO)
                .collect(Collectors.toList());

        // 打印调试信息
        System.out.println("返回AQI数据条数: " + voList.size());
        voList.forEach(vo -> {
            System.out.println("AQI " + vo.getAqiId() +
                    " SO₂: " + vo.getSo2Range() +
                    " CO: " + vo.getCoRange() +
                    " PM2.5: " + vo.getSpmRange());
        });

        return Result.success(voList);
    }

    @GetMapping("/{aqiId}")
    public Result<AqiVO> getById(@PathVariable Integer aqiId) {
        Aqi aqi = aqiService.findByAqiId(aqiId);
        if (aqi == null) {
            return Result.error("AQI等级不存在");
        }
        return Result.success(aqiService.convertToVO(aqi));
    }

    @GetMapping("/calculate")
    public Result<Integer> calculateAqi(
            @RequestParam Integer so2Value,
            @RequestParam Integer coValue,
            @RequestParam Integer spmValue) {
        Integer grade = aqiService.calculateAqiGrade(so2Value, coValue, spmValue);
        return Result.success(grade);
    }
}