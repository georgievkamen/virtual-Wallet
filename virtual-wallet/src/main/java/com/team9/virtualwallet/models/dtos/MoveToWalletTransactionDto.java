package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;

public class MoveToWalletTransactionDto extends BaseTransactionDto {

    @NotNull(message = "You must provide a wallet!")
    private Integer walletToMoveToId;

    public MoveToWalletTransactionDto() {
    }

    public Integer getWalletToMoveToId() {
        return walletToMoveToId;
    }

    public void setWalletToMoveToId(Integer walletToMoveToId) {
        this.walletToMoveToId = walletToMoveToId;
    }
}
