package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;

import java.util.List;

public interface WalletService {
    List<Wallet> getAll(User user);

    void create(User user, Wallet wallet);

    void update(User user, Wallet wallet);

    void delete(User user, int id);

    default void verifyNotDuplicate(User user, Wallet wallet) {
        if (!repository.getByFieldList("name", wallet.getName()).isEmpty()
                && repository.getByField("name", wallet.getName()).getUser().getId() != user.getId()) {
            throw new DuplicateEntityException("You already have a wallet with the same name!");
        }
    }
}
