package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;

import java.util.List;

public interface TransactionRepository extends BaseRepository<Transaction> {

    List<Transaction> getAll(User user);
}
