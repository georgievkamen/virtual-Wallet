package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum SortAmount {

    AMOUNT_ASC,
    AMOUNT_DESC;

    @Override
    public String toString() {
        switch (this) {
            case AMOUNT_ASC:
                return "Amount asc";
            case AMOUNT_DESC:
                return "Amount desc";
            default:
                throw new EnumNotFoundException("Amount should be AMOUNT_ASC or AMOUNT_DESC");
        }
    }
}