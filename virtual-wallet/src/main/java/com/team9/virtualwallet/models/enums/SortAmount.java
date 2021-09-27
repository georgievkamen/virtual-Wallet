package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum SortAmount {

    AMOUNT_ASC("Amount asc"),
    AMOUNT_DESC("Amount desc");

    public static final String INVALID_SORTAMOUNT = "Amount should be AMOUNT_ASC or AMOUNT_DESC";

    private final String value;

    SortAmount(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case AMOUNT_ASC:
                return "Amount asc";
            case AMOUNT_DESC:
                return "Amount desc";
            default:
                throw new EnumNotFoundException(INVALID_SORTAMOUNT);
        }
    }

    public static SortAmount getEnum(String value) {
        for (SortAmount v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new EnumNotFoundException(INVALID_SORTAMOUNT);
    }
}