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
import com.team9.virtualwallet.services.contracts.WalletService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;

    public TransactionServiceImpl(TransactionRepository repository, WalletRepository walletRepository, WalletService walletService) {
        this.repository = repository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
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

        if (transaction.getSender().getId() != selectedWallet.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of this wallet!");
        }

        verifyUserNotBlocked(transaction.getSender());
        // Verify recipient has a wallet
        if (transaction.getRecipient().getDefaultWallet() == null) {
            throw new IllegalArgumentException("Recipient doesn't have a default wallet set!");
        }

        // Remove Balance from Sender and deposit it to Recipient
        walletService.withdrawBalance(selectedWallet, transaction.getAmount());
        walletService.depositBalance(transaction.getRecipient().getDefaultWallet(), transaction.getAmount());
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

    //TODO Think about moving it in Wallet or UserService
    private void verifyUserNotBlocked(User user) {
        if (user.isBlocked()) {
            throw new IllegalArgumentException("You are currently blocked, you cannot make transactions");
        }
    }


}
