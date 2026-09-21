package com.neusoft.nep.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
@AllArgsConstructor
public enum SexEnum {

    FEMALE(0, "女"),
    MALE(1, "男");

    private final Integer code;
    private final String desc;

    public static SexEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SexEnum sex : values()) {
            if (sex.getCode().equals(code)) {
                return sex;
            }
        }
        return null;
    }
}