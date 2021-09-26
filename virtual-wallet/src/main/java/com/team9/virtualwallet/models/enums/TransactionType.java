package com.team9.virtualwallet.models.enums;

import com.team9.virtualwallet.exceptions.EnumNotFoundException;

public enum TransactionType {

    CARD_TO_WALLET("Card to Wallet"),
    WALLET_TO_CARD("Wallet to Card"),
    WALLET_TO_WALLET("Wallet to Wallet"),
    SMALL_TRANSACTION("Small Transaction"),
    LARGE_TRANSACTION("Large Transaction");

    public static final String INVALID_DIRECTION = "Transaction Type should be CARD_TO_WALLET, WALLET_TO_CARD, WALLET_TO_WALLET, SMALL_TRANSACTION or LARGE_TRANSACTION!";

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case CARD_TO_WALLET:
                return "Card to Wallet";
            case WALLET_TO_CARD:
                return "Wallet to Card";
            case WALLET_TO_WALLET:
                return "Wallet to Wallet";
            case SMALL_TRANSACTION:
                return "Small Transaction";
            case LARGE_TRANSACTION:
                return "Large Transaction";
            default:
                throw new EnumNotFoundException(INVALID_DIRECTION);
        }
    }

    public static TransactionType getEnum(String value) {
        for (TransactionType v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new EnumNotFoundException(INVALID_DIRECTION);
    }

}