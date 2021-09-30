package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    List<Wallet> getAll(User user);

    Wallet getById(User user, int id);

    Object getTotalBalanceByUser(User user);

    void create(User user, Wallet wallet);

    void update(User user, Wallet wallet);

    void delete(User user, int id);

    void depositBalance(Wallet wallet, BigDecimal funds);

    void withdrawBalance(Wallet wallet, BigDecimal funds);

    void verifyEnoughBalance(Wallet wallet, BigDecimal funds);

    void setDefaultWallet(User user, Wallet wallet);

    void verifyWalletOwnership(Transaction transaction, Wallet wallet);

    void verifyWalletsOwnership(Transaction transaction, Wallet walletToMoveFrom, Wallet walletToMoveTo);

    Wallet createDefaultWallet(User user);
}
