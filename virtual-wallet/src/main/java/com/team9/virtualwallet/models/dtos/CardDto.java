package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CardDto {

    //    @Size(min = 16, max = 16, message = "Card number must be 16 symbols long!")
    @Pattern(regexp = "\\d{16}")
    private String cardNumber;

    @Pattern(regexp = "(0[1-9]|1[0-2])/([0-9]{2})", message = "Invalid")
    private String expirationDate;

    @Size(min = 2, max = 40)
    private String cardHolder;

    @Size(min = 3, max = 3, message = "CVV too long!")
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
