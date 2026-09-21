package com.neusoft.nep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neusoft.nep.dto.SupervisorRegisterDTO;
import com.neusoft.nep.entity.Supervisor;
import com.neusoft.nep.enums.SexEnum;
import com.neusoft.nep.mapper.SupervisorMapper;
import com.neusoft.nep.service.SupervisorService;
import com.neusoft.nep.vo.SupervisorVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 公众监督员服务实现类
 */
@Service
public class SupervisorServiceImpl implements SupervisorService {

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Override
    public boolean register(SupervisorRegisterDTO dto) {
        // 检查手机号是否已存在
        if (isTelIdExist(dto.getTelId())) {
            throw new IllegalArgumentException("该手机号已被注册");
        }

        Supervisor supervisor = new Supervisor();
        BeanUtils.copyProperties(dto, supervisor);

        // 密码加密（实际生产环境应使用BCrypt等加密方式）
        // supervisor.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));

        int result = supervisorMapper.insert(supervisor);
        return result > 0;
    }

    @Override
    public Supervisor findByTelId(String telId) {
        return supervisorMapper.selectByTelId(telId);
    }

    @Override
    public Supervisor login(String telId, String password) {
        Supervisor supervisor = supervisorMapper.selectByTelId(telId);
        if (supervisor == null) {
            return null;
        }
        // 密码验证（实际生产环境应使用BCrypt验证）
        if (!supervisor.getPassword().equals(password)) {
            return null;
        }
        return supervisor;
    }

    @Override
    public boolean isTelIdExist(String telId) {
        return supervisorMapper.countByTelId(telId) > 0;
    }

    @Override
    public SupervisorVO convertToVO(Supervisor supervisor) {
        if (supervisor == null) {
            return null;
        }
        SupervisorVO vo = new SupervisorVO();
        BeanUtils.copyProperties(supervisor, vo);

        SexEnum sexEnum = SexEnum.getByCode(supervisor.getSex());
        if (sexEnum != null) {
            vo.setSexDesc(sexEnum.getDesc());
        }

        return vo;
    }
}