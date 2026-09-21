package com.neusoft.nep.service;

import com.neusoft.nep.dto.SupervisorRegisterDTO;
import com.neusoft.nep.entity.Supervisor;
import com.neusoft.nep.vo.SupervisorVO;

/**
 * 公众监督员服务接口
 */
public interface SupervisorService {

    /**
     * 注册公众监督员
     */
    boolean register(SupervisorRegisterDTO dto);

    /**
     * 根据手机号查询
     */
    Supervisor findByTelId(String telId);

    /**
     * 验证登录
     */
    Supervisor login(String telId, String password);

    /**
     * 检查手机号是否已注册
     */
    boolean isTelIdExist(String telId);

    /**
     * 转换为视图对象
     */
    SupervisorVO convertToVO(Supervisor supervisor);
}