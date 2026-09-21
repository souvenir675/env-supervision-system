package com.neusoft.nep.service;

import com.neusoft.nep.entity.DecisionMaker;

public interface DecisionMakerService {

    DecisionMaker findByDmCode(String dmCode);

    DecisionMaker login(String dmCode, String password);

    void updateLoginTime(Integer dmId);
}