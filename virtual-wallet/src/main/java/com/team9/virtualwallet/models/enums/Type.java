package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum Type {

    CARD("Card"),
    WALLET("Wallet");

    public static final String INVALID_DIRECTION = "Type should be Card or Wallet!";

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case CARD:
                return "Card";
            case WALLET:
                return "Wallet";
            default:
                throw new EnumNotFoundException(INVALID_DIRECTION);
        }
    }

    public static Type getEnum(String value) {
        for (Type v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new EnumNotFoundException(INVALID_DIRECTION);
    }

}