package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    List<Wallet> getAll(User user);

    Wallet getById(User user, int id);

    void create(User user, Wallet wallet);

    void update(User user, Wallet wallet);

    void delete(User user, int id);

    void depositBalance(Wallet wallet, BigDecimal balance);

    void withdrawBalance(Wallet wallet, BigDecimal balance);

}
