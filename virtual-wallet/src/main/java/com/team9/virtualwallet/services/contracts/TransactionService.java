package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.Sort;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Pages<Transaction> getAll(User user, Pageable pageable);

    List<Transaction> getLastTransactions(User user, int count);

    Transaction getById(User user, int id);

    void create(Transaction transaction, Optional<Integer> categoryId);

    void createWalletToWallet(Transaction transaction);

    void createExternalDeposit(Transaction transaction);

    void createExternalWithdraw(Transaction transaction);

    Pages<Transaction> filter(User user,
                              Optional<Direction> direction,
                              Optional<Date> startDate,
                              Optional<Date> endDate,
                              Optional<String> counterparty,
                              Optional<Sort> amount,
                              Optional<Sort> date,
                              Pageable pageable);

    Pages<Transaction> employeeFilter(User userExecuting,
                                      String username,
                                      Optional<String> counterparty,
                                      Optional<Direction> direction,
                                      Optional<Date> startDate,
                                      Optional<Date> endDate,
                                      Optional<Sort> amount,
                                      Optional<Sort> date,
                                      Pageable pageable);
}
