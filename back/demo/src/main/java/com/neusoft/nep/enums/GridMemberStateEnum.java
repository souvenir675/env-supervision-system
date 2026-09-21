package com.neusoft.nep.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 网格员状态枚举
 */
@Getter
@AllArgsConstructor
public enum GridMemberStateEnum {

    AVAILABLE(0, "可工作"),
    TEMPORARY_TRANSFER(1, "临时抽调"),
    VACATION(2, "休假"),
    OTHER(3, "其它");

    private final Integer code;
    private final String desc;

    public static GridMemberStateEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GridMemberStateEnum state : values()) {
            if (state.getCode().equals(code)) {
                return state;
            }
        }
        return null;
    }
}