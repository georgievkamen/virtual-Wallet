package com.team9.virtualwallet.exceptions;

public class CardExpiredException extends RuntimeException {

    public CardExpiredException() {
        super("Card has already expired!");
    }

}
