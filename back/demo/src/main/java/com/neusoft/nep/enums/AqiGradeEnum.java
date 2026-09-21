package com.neusoft.nep.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AQI等级枚举
 */
@Getter
@AllArgsConstructor
public enum AqiGradeEnum {

    GRADE_1(1, "一", "优", "#00E400"),
    GRADE_2(2, "二", "良", "#FFFF00"),
    GRADE_3(3, "三", "轻度污染", "#FF7E00"),
    GRADE_4(4, "四", "中度污染", "#FF0000"),
    GRADE_5(5, "五", "重度污染", "#99004C"),
    GRADE_6(6, "六", "严重污染", "#7E0023");

    private final Integer code;
    private final String chineseExplain;
    private final String aqiExplain;
    private final String color;

    public static AqiGradeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AqiGradeEnum grade : values()) {
            if (grade.getCode().equals(code)) {
                return grade;
            }
        }
        return null;
    }
}