package com.ftalk.samsu.utils.grade;

import com.ftalk.samsu.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum GradeTicketConstants {

    PROCESSING((short) 0),
    GUARANTEE((short) 1),
    APPROVED((short) 2),
    REJECTED((short) 3);

    private short value;

    GradeTicketConstants(short value) {
        this.value = value;
    }

    public static GradeTicketConstants findByValue(Short value) {
        for (GradeTicketConstants v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }

    public static Short findValue(String value) {
        for (GradeTicketConstants v : values()) {
            if (v.name().equals(value)) {
                return v.getValue();
            }
        }
        throw new BadRequestException("Not found status");
    }
}
