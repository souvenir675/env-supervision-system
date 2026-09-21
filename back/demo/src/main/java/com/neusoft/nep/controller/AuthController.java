package com.neusoft.nep.controller;

import com.neusoft.nep.common.Result;
import com.neusoft.nep.dto.LoginDTO;
import com.neusoft.nep.service.AuthService;
import com.neusoft.nep.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 添加日志
        System.out.println("登录请求: " + loginDTO.getAccount() + ", 类型: " + loginDTO.getUserType());
        LoginVO loginVO = authService.login(loginDTO);
        if (loginVO == null) {
            return Result.error("账号或密码错误");
        }
        return Result.success(loginVO);
    }
}