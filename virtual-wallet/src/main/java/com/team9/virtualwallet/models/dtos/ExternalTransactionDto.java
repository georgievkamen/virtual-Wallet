package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;

public class ExternalTransactionDto extends BaseTransactionDto {

    @NotNull(message = "You must provide a card!")
    private Integer selectedCardId;

    public ExternalTransactionDto() {
    }

    public Integer getSelectedCardId() {
        return selectedCardId;
    }

    public void setSelectedCardId(Integer selectedCardId) {
        this.selectedCardId = selectedCardId;
    }
}
