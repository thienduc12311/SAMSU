package com.ftalk.samsu.utils.event;

import com.ftalk.samsu.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum TaskConstants {

    WAITING((short) 0),
    REVIEWED((short) 1);


    private short value;

    TaskConstants(short value) {
        this.value = value;
    }

    public static TaskConstants findByValue(Short value) {
        for (TaskConstants v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }

    public static Short findValue(String value) {
        for (TaskConstants v : values()) {
            if (v.name().equals(value)) {
                return v.getValue();
            }
        }
        throw new BadRequestException("Not found status");
    }
}
