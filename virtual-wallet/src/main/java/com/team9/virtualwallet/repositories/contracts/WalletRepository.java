package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;

import java.util.List;

public interface WalletRepository extends BaseRepository<Wallet> {

    List<Wallet> getAll(User user);
}
