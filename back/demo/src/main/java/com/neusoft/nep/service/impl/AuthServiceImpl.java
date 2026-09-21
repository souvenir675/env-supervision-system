package com.neusoft.nep.service.impl;

import com.neusoft.nep.dto.LoginDTO;
import com.neusoft.nep.entity.*;
import com.neusoft.nep.service.*;
import com.neusoft.nep.util.JwtUtil;
import com.neusoft.nep.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private GridMemberService gridMemberService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private DecisionMakerService decisionMakerService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String account = loginDTO.getAccount();
        String password = loginDTO.getPassword();
        String userType = loginDTO.getUserType();

        System.out.println("登录验证 - 账号: " + account + ", 类型: " + userType);

        String userId = null;
        String userName = null;

        try {
            switch (userType) {
                case "supervisor":
                    Supervisor supervisor = supervisorService.login(account, password);
                    if (supervisor != null) {
                        userId = supervisor.getTelId();
                        userName = supervisor.getRealName();
                        System.out.println("监督员登录成功: " + userName);
                    } else {
                        System.out.println("监督员登录失败: 账号或密码错误");
                        return null;
                    }
                    break;
                case "grid":
                    GridMember gridMember = gridMemberService.login(account, password);
                    if (gridMember != null) {
                        userId = gridMember.getGmId();
                        userName = gridMember.getGmName();
                    } else {
                        return null;
                    }
                    break;
                case "admin":
                    Admin admin = adminService.login(account, password);
                    if (admin != null) {
                        userId = String.valueOf(admin.getAdminId());
                        userName = admin.getAdminCode();
                    } else {
                        return null;
                    }
                    break;
                case "decision":
                    DecisionMaker decisionMaker = decisionMakerService.login(account, password);
                    if (decisionMaker != null) {
                        userId = String.valueOf(decisionMaker.getDmId());
                        userName = decisionMaker.getDmName();
                        decisionMakerService.updateLoginTime(decisionMaker.getDmId());
                    } else {
                        return null;
                    }
                    break;
                default:
                    System.out.println("不支持的登录类型: " + userType);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("登录异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        if (userId == null) {
            return null;
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(userId, userType, userName);
        System.out.println("生成的Token: " + token);

        LoginVO vo = new LoginVO();
        vo.setUserId(userId);
        vo.setUserName(userName);
        vo.setUserType(userType);
        vo.setToken(token);
        vo.setExpireTime(jwtUtil.getExpireTime());

        System.out.println("返回LoginVO: userId=" + userId + ", userName=" + userName + ", userType=" + userType);
        return vo;
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }

    @Override
    public String getUserTypeFromToken(String token) {
        return jwtUtil.getUserTypeFromToken(token);
    }
}