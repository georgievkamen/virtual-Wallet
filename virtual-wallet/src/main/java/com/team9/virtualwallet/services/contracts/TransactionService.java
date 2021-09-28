package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    List<Transaction> getAll(User user, Pageable pageable);

    List<Transaction> getLastTransactions(User user, int count);

    Transaction getById(User user, int id);

    void create(Transaction transaction, Optional<Integer> categoryId);

    void createWalletToWallet(Transaction transaction);

    void createExternalDeposit(Transaction transaction);

    void createExternalWithdraw(Transaction transaction);

    List<Transaction> filter(User user,
                             Direction direction,
                             Optional<Date> startDate,
                             Optional<Date> endDate,
                             Optional<String> counterparty,
                             Optional<SortAmount> amount,
                             Optional<SortDate> date,
                             Pageable pageable);

    List<Transaction> employeeFilter(User userExecuting,
                                     String username,
                                     Direction direction,
                                     Optional<Date> startDate,
                                     Optional<Date> endDate,
                                     Optional<String> counterparty,
                                     Optional<SortAmount> amount,
                                     Optional<SortDate> date,
                                     Pageable pageable);
}
