package com.neusoft.nep.service;

import com.neusoft.nep.entity.Admin;

/**
 * 系统管理员服务接口
 */
public interface AdminService {

    /**
     * 根据登录编号查询
     */
    Admin findByAdminCode(String adminCode);

    /**
     * 验证登录
     */
    Admin login(String adminCode, String password);
}