package com.team9.virtualwallet.services;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
import com.team9.virtualwallet.repositories.contracts.TransactionRepository;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import com.team9.virtualwallet.services.contracts.TransactionService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final WalletRepository walletRepository;

    public TransactionServiceImpl(TransactionRepository repository, WalletRepository walletRepository) {
        this.repository = repository;
        this.walletRepository = walletRepository;
    }

    public List<Transaction> getAll(User user) {
        return repository.getAll(user);
    }

    @Override
    public Transaction getById(int id) {
        return repository.getById(id);
    }

    //TODO Add selected wallet instead of default
    @Override
    public void create(Transaction transaction, int selectedWalletId) {
        Wallet selectedWallet = walletRepository.getById(selectedWalletId);

        verifyEnoughMoneyInSelectedWallet(transaction, selectedWallet);
        verifyUserNotBlocked(transaction.getSender());
        // Verify recipient has a wallet
        if (transaction.getRecipient().getDefaultWallet() == null) {
            throw new IllegalArgumentException("Recipient doesn't have a default wallet set!");
        }

        // Remove Balance from Sender and deposit it to Recipient
        selectedWallet.withdrawBalance(transaction.getAmount());
        transaction.getRecipient().getDefaultWallet().depositBalance(transaction.getAmount());
        repository.create(transaction);
    }

    @Override
    public List<Transaction> filter(User user,
                                    Optional<Date> startDate,
                                    Optional<Date> endDate,
                                    Optional<Integer> senderId,
                                    Optional<Integer> recipientId,
                                    Optional<Direction> direction,
                                    Optional<SortAmount> amount,
                                    Optional<SortDate> date) {

        return repository.filter(user.getId(), startDate, endDate, senderId, recipientId, direction, amount, date);
    }

    private void verifyEnoughMoneyInSelectedWallet(Transaction transaction, Wallet selectedWallet) {
        //TODO Check if it works properly and add selected wallet instead of default
        if (selectedWallet.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new IllegalArgumentException("You do not have enough money in the selected wallet!");
        }
    }

    private void verifyUserNotBlocked(User user) {
        if (user.isBlocked()) {
            throw new IllegalArgumentException("You are currently blocked, you cannot make transactions");
        }
    }


}
