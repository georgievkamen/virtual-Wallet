package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum Sort {

    ASC("Asc"),
    DESC("Desc");

    public static final String INVALID_SORT = "Sort should be ASC or DESC";

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
                return "Asc";
            case DESC:
                return "Desc";
            default:
                throw new EnumNotFoundException(INVALID_SORT);
        }
    }

    public static Sort getEnum(String value) {
        for (Sort v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new EnumNotFoundException(INVALID_SORT);
    }
}