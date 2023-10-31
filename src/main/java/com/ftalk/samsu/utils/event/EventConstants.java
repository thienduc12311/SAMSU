package com.ftalk.samsu.utils.event;

import lombok.Getter;

@Getter
public enum EventConstants {

    PENDING((short) 0),
    ACCEPTED((short) 1),
    PROGRESSION((short) 2),
    CANCELED((short) 3);

    private short value;

    EventConstants(short value) {
        this.value = value;
    }

    public static EventConstants findByValue(short value) {
        for (EventConstants v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }
}
