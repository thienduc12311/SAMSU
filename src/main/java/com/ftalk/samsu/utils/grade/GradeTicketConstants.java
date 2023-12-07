package com.ftalk.samsu.utils.grade;

import com.ftalk.samsu.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum GradeTicketConstants {

    PROCESSING((short) 0),
    GUARANTEE_ACCEPT((short) 1),
    GUARANTEE_REJECT((short) 2),
    APPROVED((short) 3),
    REJECTED((short) 4);

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
