package com.team9.virtualwallet.exceptions;

import java.time.LocalDate;

public class CardExpiredException extends RuntimeException {

    public CardExpiredException(String cardNumber, LocalDate expirationDate) {
        super(String.format("Card with number %s is expired on %s", cardNumber, expirationDate));
    }

}
