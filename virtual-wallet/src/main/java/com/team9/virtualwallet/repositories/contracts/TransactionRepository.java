package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.Sort;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends BaseRepository<Transaction> {

    Pages<Transaction> getAll(User user, Pageable pageable);

    List<Transaction> getLastTransactions(User user, int count);

    void create(Transaction transaction, Wallet walletToWithdraw, Wallet walletToDeposit);

    void createExternal(Transaction transaction, Wallet wallet);

    void update(Transaction transaction, Wallet walletToWithdraw, Wallet walletToDeposit);

    Pages<Transaction> filter(int userId,
                              Optional<Direction> direction,
                              Optional<Date> startDate,
                              Optional<Date> endDate,
                              Optional<Integer> searchedPersonId,
                              Optional<Sort> amount,
                              Optional<Sort> date,
                              Pageable pageable);
}
