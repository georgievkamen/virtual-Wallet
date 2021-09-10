package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;

import java.util.List;

public interface WalletService {
    List<Wallet> getAll(User user);

    void create(User user, Wallet wallet);

    void update(User user, Wallet wallet);

    void delete(User user, int id);

}
