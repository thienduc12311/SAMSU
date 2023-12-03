package com.ftalk.samsu.utils.event;

import com.ftalk.samsu.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum AssigneeConstants {

    WAITING((short) 0),
    ACCEPT((short) 1),
    REJECT((short) 2),
    COMPLETE((short) 3),
    APPROVED((short) 4),
    DISAPPROVED((short) 5);


    private short value;

    AssigneeConstants(short value) {
        this.value = value;
    }

    public static AssigneeConstants findByValue(Short value) {
        for (AssigneeConstants v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }

    public static Short findValue(String value) {
        for (AssigneeConstants v : values()) {
            if (v.name().equals(value)) {
                return v.getValue();
            }
        }
        throw new BadRequestException("Not found status");
    }
}
