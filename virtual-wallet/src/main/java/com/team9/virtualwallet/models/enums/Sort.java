package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum Sort {

    ASC("Ascending"),
    DESC("Descending");

    public static final String INVALID_CATEGORY = "Sort should be ASCENDING or DESCENDING";

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case ASC:
                return "Ascending";
            case DESC:
                return "Descending";
            default:
                throw new EnumNotFoundException(INVALID_CATEGORY);
        }
    }

    public static Sort getEnum(String value) {
        for (Sort v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new EnumNotFoundException(INVALID_CATEGORY);
    }

}