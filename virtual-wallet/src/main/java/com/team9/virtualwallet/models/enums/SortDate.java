package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum SortDate {

    DATE_ASC,
    DATE_DESC;

    @Override
    public String toString() {
        switch (this) {
            case DATE_ASC:
                return "Date asc";
            case DATE_DESC:
                return "Date desc";
            default:
                throw new EnumNotFoundException("Date should be DATE_ASC or DATE_DESC");
        }
    }
}