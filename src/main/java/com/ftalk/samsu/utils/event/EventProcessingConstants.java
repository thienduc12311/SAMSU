package com.ftalk.samsu.utils.event;

import com.ftalk.samsu.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum EventProcessingConstants {

    COMING((short) 0),
    CHECK_IN((short) 1),
    PROCESSING((short) 2),
    CHECK_OUT((short) 3),
    COMPLETE((short) 4),
    REVIEWED((short) 5),
    FINISH((short) 6),
    CANCEL((short) 7);

    private short value;

    EventProcessingConstants(short value) {
        this.value = value;
    }

    public static EventProcessingConstants findByValue(Short value) {
        for (EventProcessingConstants v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }

    public static Short findValue(String value) {
        for (EventProcessingConstants v : values()) {
            if (v.name().equals(value)) {
                return v.getValue();
            }
        }
        throw new BadRequestException("Not found status");
    }
}
