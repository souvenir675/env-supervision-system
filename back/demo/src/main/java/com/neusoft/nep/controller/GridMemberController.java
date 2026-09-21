package com.neusoft.nep.controller;

import com.neusoft.nep.common.Result;
import com.neusoft.nep.dto.GridMemberRegisterDTO;
import com.neusoft.nep.dto.GridMemberStateUpdateDTO;
import com.neusoft.nep.entity.GridMember;
import com.neusoft.nep.service.GridMemberService;
import com.neusoft.nep.vo.GridMemberVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 网格员控制器
 */
@RestController
@RequestMapping("/api/gridmember")
public class GridMemberController {

    @Autowired
    private GridMemberService gridMemberService;

    /**
     * 管理员注册网格员
     */
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody GridMemberRegisterDTO dto) {
        try {
            boolean result = gridMemberService.register(dto);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 网格员修改自己的工作状态
     */
    @PutMapping("/state")
    public Result<Boolean> updateState(@Valid @RequestBody GridMemberStateUpdateDTO dto) {
        try {
            boolean result = gridMemberService.updateState(dto);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询所有网格员（管理员）
     */
    @GetMapping("/list")
    public Result<List<GridMemberVO>> list(
            @RequestParam(required = false) Integer provinceId) {
        List<GridMember> list;
        if (provinceId != null) {
            list = gridMemberService.findByProvince(provinceId);
        } else {
            list = gridMemberService.findAll();
        }
        List<GridMemberVO> voList = list.stream()
                .map(gridMemberService::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 删除网格员（管理员）
     */
    @DeleteMapping("/delete/{gmId}")
    public Result<Boolean> delete(@PathVariable String gmId) {
        boolean result = gridMemberService.delete(gmId);
        return Result.success(result);
    }

    /**
     * 获取可工作的网格员列表（用于指派）
     */
    @GetMapping("/available")
    public Result<List<GridMemberVO>> getAvailableList() {
        List<GridMember> list = gridMemberService.findAllAvailable();
        List<GridMemberVO> voList = list.stream()
                .map(gridMemberService::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 根据区域获取可工作的网格员
     */
    @GetMapping("/available/area")
    public Result<List<GridMemberVO>> getAvailableByArea(
            @PathVariable Integer provinceId,
            @PathVariable Integer cityId) {
        List<GridMember> list = gridMemberService.findAvailableByArea(provinceId, cityId);
        List<GridMemberVO> voList = list.stream()
                .map(gridMemberService::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 查询网格员详情
     */
    @GetMapping("/{gmId}")
    public Result<GridMemberVO> getDetail(@PathVariable String gmId) {
        GridMember gridMember = gridMemberService.findByGmId(gmId);
        if (gridMember == null) {
            return Result.error("网格员不存在");
        }
        return Result.success(gridMemberService.convertToVO(gridMember));
    }
}