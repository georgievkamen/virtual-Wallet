package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
import com.team9.virtualwallet.models.enums.TransactionType;
import com.team9.virtualwallet.repositories.contracts.CardRepository;
import com.team9.virtualwallet.repositories.contracts.TransactionRepository;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import com.team9.virtualwallet.services.contracts.CategoryService;
import com.team9.virtualwallet.services.contracts.TransactionService;
import com.team9.virtualwallet.services.contracts.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.team9.virtualwallet.services.utils.Helpers.validateCardExpiryDate;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final WalletRepository walletRepository;
    private final CardRepository cardRepository;
    private final WalletService walletService;
    private final CategoryService categoryService;

    public TransactionServiceImpl(TransactionRepository repository, WalletRepository walletRepository, CardRepository cardRepository, WalletService walletService, CategoryService categoryService) {
        this.repository = repository;
        this.walletRepository = walletRepository;
        this.cardRepository = cardRepository;
        this.walletService = walletService;
        this.categoryService = categoryService;
    }

    @Override
    public List<Transaction> getAll(User user) {
        return repository.getAll(user);
    }

    @Override
    public List<Transaction> getLastTransactions(User user, int count) {
        return repository.getLastTransactions(user, count);
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
    public void create(Transaction transaction, Optional<Integer> categoryId) {
        Wallet senderWallet = walletRepository.getById(transaction.getSenderPaymentMethod().getId());
        Wallet recipientWallet = transaction.getRecipient().getDefaultWallet();

        verifyWalletOwnership(transaction, senderWallet);

        //TODO Fix this || Create a method for transfer from one wallet to another
        if (transaction.getSender().getId() == transaction.getRecipient().getId()) {
            throw new IllegalArgumentException("You can't send money to yourself!");
        }

        verifyUserNotBlocked(transaction.getSender());
        walletService.verifyEnoughBalance(senderWallet, transaction.getAmount());

        categoryId.ifPresent(integer -> transaction.setCategory(categoryService.getById(transaction.getSender(), integer)));

        senderWallet.withdrawBalance(transaction.getAmount());
        recipientWallet.depositBalance(transaction.getAmount());

        if (transaction.getAmount().compareTo(BigDecimal.valueOf(100000)) >= 0) {
            transaction.setTransactionType(TransactionType.LARGE_TRANSACTION);
        } else {
            transaction.setTransactionType(TransactionType.SMALL_TRANSACTION);
        }

        repository.create(transaction, senderWallet, recipientWallet);
    }

    @Override
    public void createExternalDeposit(Transaction transaction, Optional<Integer> categoryId) {
        Card cardToWithdraw = cardRepository.getById(transaction.getSenderPaymentMethod().getId());
        validateCardExpiryDate(cardToWithdraw);
        Wallet walletToDeposit = walletRepository.getById(transaction.getRecipientPaymentMethod().getId());

        verifyWalletOwnership(transaction, walletToDeposit);

        verifyCardOwnership(transaction, cardToWithdraw);

        categoryId.ifPresent(integer -> transaction.setCategory(categoryService.getById(transaction.getSender(), integer)));

        walletToDeposit.depositBalance(transaction.getAmount());
        repository.createExternal(transaction, walletToDeposit);
    }

    @Override
    public void createExternalWithdraw(Transaction transaction, Optional<Integer> categoryId) {
        Wallet walletToWithdraw = walletRepository.getById(transaction.getSenderPaymentMethod().getId());
        Card cardToDeposit = cardRepository.getById(transaction.getRecipientPaymentMethod().getId());
        validateCardExpiryDate(cardToDeposit);

        verifyWalletOwnership(transaction, walletToWithdraw);
        verifyCardOwnership(transaction, cardToDeposit);

        walletService.verifyEnoughBalance(walletToWithdraw, transaction.getAmount());

        categoryId.ifPresent(integer -> transaction.setCategory(categoryService.getById(transaction.getSender(), integer)));

        walletToWithdraw.withdrawBalance(transaction.getAmount());
        repository.createExternal(transaction, walletToWithdraw);
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

    private void verifyWalletOwnership(Transaction transaction, Wallet wallet) {
        if (transaction.getSender().getId() != wallet.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of this wallet!");
        }
    }

    private void verifyCardOwnership(Transaction transaction, Card card) {
        if (transaction.getRecipient().getId() != card.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of this card!");
        }
    }

}
