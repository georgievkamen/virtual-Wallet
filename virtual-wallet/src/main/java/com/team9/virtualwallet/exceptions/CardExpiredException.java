package com.team9.virtualwallet.exceptions;

public class CardExpiredException extends RuntimeException {

    public CardExpiredException(String cardNumber) {
        super(String.format("Card with number %s has expired!", cardNumber));
    }

}
