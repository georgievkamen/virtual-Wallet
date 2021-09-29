package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum Direction {

    INCOMING("Incoming"),
    OUTGOING("Outgoing"),
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    WALLET_TO_WALLET("Wallet to Wallet");

    public static final String INVALID_DIRECTION = "Direction should be INCOMING, OUTGOING, DEPOSIT, WITHDRAW or WALLET_TO_WALLET";

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
            case DEPOSIT:
                return "Deposit";
            case WITHDRAW:
                return "Withdraw";
            case WALLET_TO_WALLET:
                return "Wallet to Wallet";
            default:
                throw new EnumNotFoundException(INVALID_DIRECTION);
        }
    }

    public static Direction getEnum(String value) {
        for (Direction v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new EnumNotFoundException(INVALID_DIRECTION);
    }

}