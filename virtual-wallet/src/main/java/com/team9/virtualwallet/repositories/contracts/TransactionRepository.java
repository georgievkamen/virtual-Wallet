package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends BaseRepository<Transaction> {

    List<Transaction> getAll(User user, Pageable pageable);

    List<Transaction> getLastTransactions(User user, int count);

    void create(Transaction transaction, Wallet walletToWithdraw, Wallet walletToDeposit);

    void createExternal(Transaction transaction, Wallet wallet);

    List<Transaction> filter(int userId,
                             Direction direction,
                             Optional<Date> startDate,
                             Optional<Date> endDate,
                             Optional<Integer> searchedPersonId,
                             Optional<SortAmount> amount,
                             Optional<SortDate> date,
                             Pageable pageable);
}
