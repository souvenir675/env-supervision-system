package com.neusoft.nep.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账号状态枚举
 */
@Getter
@AllArgsConstructor
public enum AccountStatusEnum {

    DISABLED(0, "停用"),
    ENABLED(1, "启用");

    private final Integer code;
    private final String desc;

    public static AccountStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AccountStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}