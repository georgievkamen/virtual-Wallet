package com.team9.virtualwallet.services.mappers;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.TransactionDto;
import com.team9.virtualwallet.repositories.contracts.TransactionRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class TransactionModelMapper {

    private final UserRepository userRepository;
    private final TransactionRepository repository;


    @Autowired
    public TransactionModelMapper(UserRepository userRepository, TransactionRepository repository) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    public Transaction fromDto(User user, TransactionDto transactionDto) {
        Transaction transaction = new Transaction();

        dtoToObject(transactionDto, transaction);
        transaction.setSender(user);

        return transaction;
    }

    public Transaction fromDto(TransactionDto transactionDto, int id) {
        Transaction transaction = repository.getById(id);

        dtoToObject(transactionDto, transaction);

        return transaction;
    }

    private void dtoToObject(TransactionDto transactionDto, Transaction transaction) {

        LocalDateTime localDateTime = LocalDateTime.now();
        User recipient = userRepository.getById(transactionDto.getRecipientId());

        transaction.setRecipient(recipient);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTimestamp(Timestamp.valueOf(localDateTime));

    }

}
