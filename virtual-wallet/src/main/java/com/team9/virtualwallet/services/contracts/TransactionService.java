package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    List<Transaction> getAll(User user);

    List<Transaction> getLastTransactions(User user, int count);

    Transaction getById(User user, int id);

    void create(Transaction transaction, int selectedWalletId, Optional<Integer> categoryId);

    void createExternalDeposit(Transaction transaction, Optional<Integer> categoryId);

    void createExternalWithdraw(Transaction transaction, Optional<Integer> categoryId);

    List<Transaction> filter(User user,
                             Optional<Date> startDate,
                             Optional<Date> endDate,
                             Optional<Integer> categoryId,
                             Optional<Integer> senderId,
                             Optional<Integer> recipientId,
                             Optional<Direction> direction,
                             Optional<SortAmount> amount,
                             Optional<SortDate> date);
}
