package com.neusoft.nep.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 反馈状态枚举
 */
@Getter
@AllArgsConstructor
public enum FeedbackStateEnum {

    UNASSIGNED(0, "未指派"),
    ASSIGNED(1, "已指派"),
    CONFIRMED(2, "已确认");

    private final Integer code;
    private final String desc;

    public static FeedbackStateEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FeedbackStateEnum state : values()) {
            if (state.getCode().equals(code)) {
                return state;
            }
        }
        return null;
    }
}