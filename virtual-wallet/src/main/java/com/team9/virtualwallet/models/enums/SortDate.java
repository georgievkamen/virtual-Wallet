package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum SortDate {

    DATE_ASC("Date asc"),
    DATE_DESC("Date desc");

    public static final String INVALID_SORTDATE = "Date should be DATE_ASC or DATE_DESC";

    private final String value;

    SortDate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        switch (this) {
            case DATE_ASC:
                return "Date asc";
            case DATE_DESC:
                return "Date desc";
            default:
                throw new EnumNotFoundException(INVALID_SORTDATE);
        }
    }

    public static SortDate getEnum(String value) {
        for (SortDate v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new EnumNotFoundException(INVALID_SORTDATE);
    }

}
