package com.ftalk.samsu.utils.event;

import lombok.Getter;

public enum EventProposalConstants {

    PENDING((short) 0),
    ACCEPTED((short) 1),
    WAITING_APPROVE((short) 2),
    DENIED((short) 3);

    private short value;

    public short getValue() {
        return value;
    }

    EventProposalConstants(short value) {
        this.value = value;
    }

    public static EventProposalConstants findByValue(short value) {
        for (EventProposalConstants v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }
}
