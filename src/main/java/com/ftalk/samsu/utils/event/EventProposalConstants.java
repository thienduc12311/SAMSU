package com.ftalk.samsu.utils.event;

import com.ftalk.samsu.exception.BadRequestException;
import lombok.Getter;

public enum EventProposalConstants {

    PROCESSING((short) 0),
    REVIEWED((short) 1),
    APPROVED((short) 2),
    REJECTED((short) 3);

    private short value;

    public short getValue() {
        return value;
    }

    EventProposalConstants(short value) {
        this.value = value;
    }

    public static EventProposalConstants findByValue(Short value) {
        for (EventProposalConstants v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }

    public static Short findValue(String value) {
        for (EventProposalConstants v : values()) {
            if (v.name().equals(value)) {
                return v.getValue();
            }
        }
        throw new BadRequestException("Not found status");
    }
}
