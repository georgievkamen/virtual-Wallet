package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CardDto {

    @NotNull(message = "You must provide a card number!")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 numbers long!")
    private String cardNumber;

    @NotNull(message = "You must provide an expiration date!")
    @Pattern(regexp = "(0[1-9]|1[0-2])/([0-9]{2})", message = "You must provide a correct expiration date in [MM/YY] format!")
    private String expirationDate;

    @NotNull(message = "You must provide a card holder!")
    @Size(min = 2, max = 40, message = "Card holder must be between 2 and 40 characters long!")
    private String cardHolder;

    @NotNull(message = "You must provide a CVV!")
    @Size(min = 3, max = 3, message = "CVV must be 3 numbers long!")
    private String cvv;

    public CardDto() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
