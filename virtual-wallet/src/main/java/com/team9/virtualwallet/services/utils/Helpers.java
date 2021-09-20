package com.team9.virtualwallet.services.utils;

import com.team9.virtualwallet.exceptions.CardExpiredException;
import com.team9.virtualwallet.models.Card;

import java.time.LocalDate;

public class Helpers {

    public static void validateCardExpiryDate(Card card) {
        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new CardExpiredException(card.getCardNumber(), card.getExpirationDate());
        }
    }
}
