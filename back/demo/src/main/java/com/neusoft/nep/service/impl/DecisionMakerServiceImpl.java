package com.neusoft.nep.service.impl;

import com.neusoft.nep.entity.DecisionMaker;
import com.neusoft.nep.mapper.DecisionMakerMapper;
import com.neusoft.nep.service.DecisionMakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class DecisionMakerServiceImpl implements DecisionMakerService {

    @Autowired
    private DecisionMakerMapper decisionMakerMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public DecisionMaker findByDmCode(String dmCode) {
        return decisionMakerMapper.selectByDmCode(dmCode);
    }

    @Override
    public DecisionMaker login(String dmCode, String password) {
        DecisionMaker decisionMaker = decisionMakerMapper.selectByDmCode(dmCode);
        if (decisionMaker == null) {
            return null;
        }
        if (!decisionMaker.getPassword().equals(password)) {
            return null;
        }
        if (decisionMaker.getStatus() == null || decisionMaker.getStatus() == 0) {
            return null;
        }
        return decisionMaker;
    }

    @Override
    public void updateLoginTime(Integer dmId) {
        if (dmId == null) {
            return;
        }
        try {
            LocalDate now = LocalDate.now();
            LocalTime time = LocalTime.now();
            Integer result = decisionMakerMapper.updateLoginTime(
                    dmId,
                    now.format(DATE_FORMATTER),
                    time.format(TIME_FORMATTER)
            );
            System.out.println("更新登录时间结果: " + (result != null && result > 0 ? "成功" : "失败"));
        } catch (Exception e) {
            System.err.println("更新登录时间失败: " + e.getMessage());
            // 不影响登录流程，仅记录日志
        }
    }
}