package com.neusoft.nep.controller;

import com.neusoft.nep.common.Result;
import com.neusoft.nep.dto.SupervisorRegisterDTO;
import com.neusoft.nep.entity.Supervisor;
import com.neusoft.nep.service.SupervisorService;
import com.neusoft.nep.vo.SupervisorVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



/**
 * 公众监督员控制器
 */
@RestController
@RequestMapping("/api/supervisor")
@Validated
public class SupervisorController {

    @Autowired
    private SupervisorService supervisorService;

    /**
     * 公众监督员注册
     */
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody SupervisorRegisterDTO dto) {
        try {
            boolean result = supervisorService.register(dto);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询监督员信息
     */
    @GetMapping("/{telId}")
    public Result<SupervisorVO> getInfo(@PathVariable String telId) {
        Supervisor supervisor = supervisorService.findByTelId(telId);
        if (supervisor == null) {
            return Result.error("用户不存在");
        }
        return Result.success(supervisorService.convertToVO(supervisor));
    }
}