package com.ftalk.samsu.utils.event;

import com.ftalk.samsu.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum EventConstants {

    NON_PUBLIC((short) 0),
    PUBLIC((short) 1),
    DRAFT((short) 2);

    private short value;

    EventConstants(short value) {
        this.value = value;
    }

    public static EventConstants findByValue(Short value) {
        for (EventConstants v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }

    public static Short findValue(String value) {
        for (EventConstants v : values()) {
            if (v.name().equals(value)) {
                return v.getValue();
            }
        }
        throw new BadRequestException("Not found status");
    }
}
