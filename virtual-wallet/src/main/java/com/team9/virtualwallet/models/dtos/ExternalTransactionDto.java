package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;

public class ExternalTransactionDto extends BaseTransactionDto {

    @NotNull(message = "You must provide a card!")
    private Integer selectedCardId;

    public ExternalTransactionDto() {
    }

    public int getSelectedCardId() {
        return selectedCardId;
    }

    public void setSelectedCardId(int selectedCardId) {
        this.selectedCardId = selectedCardId;
    }

}
