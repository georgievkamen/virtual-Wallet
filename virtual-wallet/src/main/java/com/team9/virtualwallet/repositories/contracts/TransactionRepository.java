package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends BaseRepository<Transaction> {

    List<Transaction> getAll(User user);

    public List<Transaction> filter(int userId,
                                    Optional<Date> startDate,
                                    Optional<Date> endDate,
                                    Optional<Integer> senderId,
                                    Optional<Integer> recipientId,
                                    Optional<Direction> direction,
                                    Optional<SortAmount> amount,
                                    Optional<SortDate> date);
}
