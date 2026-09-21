package com.neusoft.nep.service.impl;

import com.neusoft.nep.entity.Admin;
import com.neusoft.nep.mapper.AdminMapper;
import com.neusoft.nep.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin findByAdminCode(String adminCode) {
        return adminMapper.selectByAdminCode(adminCode);
    }

    @Override
    public Admin login(String adminCode, String password) {
        Admin admin = adminMapper.selectByAdminCode(adminCode);
        if (admin == null) {
            return null;
        }
        if (!admin.getPassword().equals(password)) {
            return null;
        }
        return admin;
    }
}