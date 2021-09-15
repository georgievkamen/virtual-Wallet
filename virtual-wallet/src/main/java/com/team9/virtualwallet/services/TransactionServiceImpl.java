package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
import com.team9.virtualwallet.repositories.contracts.PaymentMethodRepository;
import com.team9.virtualwallet.repositories.contracts.TransactionRepository;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import com.team9.virtualwallet.services.contracts.CategoryService;
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
    private final PaymentMethodRepository paymentMethodRepository;
    private final WalletService walletService;
    private final CategoryService categoryService;

    public TransactionServiceImpl(TransactionRepository repository, WalletRepository walletRepository, PaymentMethodRepository paymentMethodRepository, WalletService walletService, CategoryService categoryService) {
        this.repository = repository;
        this.walletRepository = walletRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.walletService = walletService;
        this.categoryService = categoryService;
    }

    public List<Transaction> getAll(User user) {
        return repository.getAll(user);
    }

    @Override
    public Transaction getById(User user, int id) {
        Transaction transaction = repository.getById(id);

        if (!user.isEmployee() && transaction.getSender().getId() != user.getId() && transaction.getRecipient().getId() != user.getId()) {
            throw new UnauthorizedOperationException("You are not the sender or recipient of this transaction!");
        }

        return repository.getById(id);
    }

    @Override
    public void create(Transaction transaction, int selectedWalletId, Optional<Integer> categoryId) {
        Wallet senderWallet = walletRepository.getById(selectedWalletId);
        Wallet recipientWallet = transaction.getRecipient().getDefaultWallet();

        if (transaction.getSender().getId() != senderWallet.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of this wallet!");
        }

        //TODO Fix this || Create a method for transfer from one wallet to another
        if (transaction.getSender().getId() == transaction.getRecipient().getId()) {
            throw new IllegalArgumentException("You can't send money to yourself!");
        }

        verifyUserNotBlocked(transaction.getSender());
        walletService.verifyEnoughBalance(senderWallet, transaction.getAmount());
        verifyRecipientHasDefaultWallet(transaction);

        categoryId.ifPresent(integer -> transaction.setCategory(categoryService.getById(transaction.getSender(), integer)));

        senderWallet.withdrawBalance(transaction.getAmount());
        recipientWallet.depositBalance(transaction.getAmount());

        repository.create(transaction, senderWallet, recipientWallet);
    }

    @Override
    public void createExternalDeposit(Transaction transaction, int selectedWalletId, int cardId, boolean rejected, Optional<Integer> categoryId) {
        if (rejected) {
            throw new IllegalArgumentException("Sorry your transfer is rejected");
        }
        //TODO Add checks if user is trying to transfer money to himself
        Wallet selectedWallet = walletRepository.getById(selectedWalletId);

        if (transaction.getSender().getId() != selectedWallet.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of this wallet!");
        }
        categoryId.ifPresent(integer -> transaction.setCategory(categoryService.getById(transaction.getSender(), integer)));

        walletService.depositBalance(selectedWallet, transaction.getAmount());
        repository.create(transaction);
    }

    @Override
    public void createExternalWithdraw(Transaction transaction, int selectedWalletId, int cardId, Optional<Integer> categoryId) {
        //TODO Add checks if user is trying to transfer money to himself
        Wallet selectedWallet = walletRepository.getById(selectedWalletId);

        if (transaction.getSender().getId() != selectedWallet.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of this wallet!");
        }
        categoryId.ifPresent(integer -> transaction.setCategory(categoryService.getById(transaction.getSender(), integer)));

        walletService.withdrawBalance(selectedWallet, transaction.getAmount());
        repository.create(transaction);
    }

    @Override
    public List<Transaction> filter(User user,
                                    Optional<Date> startDate,
                                    Optional<Date> endDate,
                                    Optional<Integer> categoryId,
                                    Optional<Integer> senderId,
                                    Optional<Integer> recipientId,
                                    Optional<Direction> direction,
                                    Optional<SortAmount> amount,
                                    Optional<SortDate> date) {

        return repository.filter(user.getId(), startDate, endDate, categoryId, senderId, recipientId, direction, amount, date);
    }

    //TODO Think about moving it in Wallet or UserService

    private void verifyUserNotBlocked(User user) {
        if (user.isBlocked()) {
            throw new IllegalArgumentException("You are currently blocked, you cannot make transactions");
        }
    }

    private void verifyRecipientHasDefaultWallet(Transaction transaction) {
        if (transaction.getRecipient().getDefaultWallet() == null) {
            throw new IllegalArgumentException("Recipient doesn't have a default wallet set!");
        }
    }

}
