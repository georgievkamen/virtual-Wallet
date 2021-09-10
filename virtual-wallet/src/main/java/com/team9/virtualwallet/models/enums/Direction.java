package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum Direction {

    INCOMING("Incoming"),
    OUTGOING("Outgoing");

    public static final String INVALID_CATEGORY = "Direction should be INCOMING or OUTGOING";

    private final String value;

    Direction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case INCOMING:
                return "Incoming";
            case OUTGOING:
                return "Outgoing";
            default:
                throw new EnumNotFoundException(INVALID_CATEGORY);
        }
    }

    public static Direction getEnum(String value) {
        for (Direction v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new EnumNotFoundException(INVALID_CATEGORY);
    }

}