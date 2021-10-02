package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.*;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.Sort;
import com.team9.virtualwallet.models.enums.TransactionType;
import com.team9.virtualwallet.repositories.contracts.*;
import com.team9.virtualwallet.services.contracts.*;
import com.team9.virtualwallet.services.emails.SendEmailService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.team9.virtualwallet.configs.ApplicationConstants.LARGE_TRANSACTION_AMOUNT;
import static com.team9.virtualwallet.services.utils.Helpers.validateCardExpiryDate;
import static com.team9.virtualwallet.services.utils.MessageConstants.UNAUTHORIZED_ACTION;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final WalletRepository walletRepository;
    private final CardService cardService;
    private final CardRepository cardRepository;
    private final WalletService walletService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TransactionVerificationTokenRepository transactionVerificationTokenRepository;
    private final SendEmailService sendEmailService;

    public TransactionServiceImpl(TransactionRepository repository, WalletRepository walletRepository, CardService cardService, CardRepository cardRepository, WalletService walletService, CategoryService categoryService, UserRepository userRepository, UserService userService, TransactionVerificationTokenRepository transactionVerificationTokenRepository, SendEmailService sendEmailService) {
        this.repository = repository;
        this.walletRepository = walletRepository;
        this.cardService = cardService;
        this.cardRepository = cardRepository;
        this.walletService = walletService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.transactionVerificationTokenRepository = transactionVerificationTokenRepository;
        this.sendEmailService = sendEmailService;
    }

    @Override
    public Pages<Transaction> getAll(User user, Pageable pageable) {
        return repository.getAll(user, pageable);
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

        walletService.verifyWalletOwnership(transaction, senderWallet);

        if (transaction.getSender().getId() == transaction.getRecipient().getId()) {
            throw new IllegalArgumentException("You can't send money to yourself!");
        }
        verifyUserCanMakeTransactions(transaction.getSender());

        walletService.verifyEnoughBalance(senderWallet, transaction.getAmount());

        categoryId.ifPresent(integer -> transaction.setCategory(categoryService.getById(transaction.getSender(), integer)));

        if (transaction.getAmount().compareTo(BigDecimal.valueOf(LARGE_TRANSACTION_AMOUNT)) >= 0) {
            transaction.setTransactionType(TransactionType.LARGE_UNVERIFIED);
            repository.create(transaction, senderWallet, recipientWallet);
            sendEmailService.sendEmailTransactionVerification(transaction);
        } else {
            transaction.setTransactionType(TransactionType.SMALL_TRANSACTION);
            senderWallet.withdrawBalance(transaction.getAmount());
            recipientWallet.depositBalance(transaction.getAmount());
            repository.create(transaction, senderWallet, recipientWallet);
        }

    }

    @Override
    public void confirmLargeTransaction(User user, String transactionVerificationToken) {
        TransactionVerificationToken token = transactionVerificationTokenRepository.getByField("verificationToken", transactionVerificationToken);

        if (user.getId() != token.getTransaction().getSender().getId()) {
            throw new UnauthorizedOperationException("You cannot verify transaction that is not yours!");
        }

        Transaction transaction = token.getTransaction();
        if (Timestamp.valueOf(LocalDateTime.now()).before(token.getExpirationDate())) {
            verifyUserCanMakeTransactions(transaction.getSender());
            Wallet senderWallet = walletRepository.getById(transaction.getSenderPaymentMethod().getId());
            walletService.verifyEnoughBalance(senderWallet, transaction.getAmount());
            Wallet recipientWallet = transaction.getRecipient().getDefaultWallet();
            senderWallet.withdrawBalance(transaction.getAmount());
            recipientWallet.depositBalance(transaction.getAmount());
            transaction.setTransactionType(TransactionType.LARGE_TRANSACTION);
            repository.update(transaction, senderWallet, recipientWallet);
        }
    }

    @Override
    public void createWalletToWallet(Transaction transaction) {
        Wallet walletToMoveFrom = walletRepository.getById(transaction.getSenderPaymentMethod().getId());
        Wallet walletToMoveTo = walletRepository.getById(transaction.getRecipientPaymentMethod().getId());

        walletService.verifyWalletsOwnership(transaction, walletToMoveFrom, walletToMoveTo);

        if (walletToMoveTo.getId() == walletToMoveFrom.getId()) {
            throw new IllegalArgumentException("You must select a different wallet!");
        }
        walletService.verifyEnoughBalance(walletToMoveFrom, transaction.getAmount());

        walletToMoveFrom.withdrawBalance(transaction.getAmount());
        walletToMoveTo.depositBalance(transaction.getAmount());

        transaction.setTransactionType(TransactionType.WALLET_TO_WALLET);

        repository.create(transaction, walletToMoveFrom, walletToMoveTo);
    }


    @Override
    public void createExternalDeposit(Transaction transaction) {
        Card cardToWithdraw = cardRepository.getById(transaction.getSenderPaymentMethod().getId());
        validateCardExpiryDate(cardToWithdraw);
        Wallet walletToDeposit = walletRepository.getById(transaction.getRecipientPaymentMethod().getId());

        walletService.verifyWalletOwnership(transaction, walletToDeposit);

        cardService.verifyCardOwnership(transaction, cardToWithdraw);

        walletToDeposit.depositBalance(transaction.getAmount());
        repository.createExternal(transaction, walletToDeposit);
    }

    @Override
    public void createExternalWithdraw(Transaction transaction) {
        Wallet walletToWithdraw = walletRepository.getById(transaction.getSenderPaymentMethod().getId());
        Card cardToDeposit = cardRepository.getById(transaction.getRecipientPaymentMethod().getId());
        validateCardExpiryDate(cardToDeposit);

        walletService.verifyWalletOwnership(transaction, walletToWithdraw);
        cardService.verifyCardOwnership(transaction, cardToDeposit);

        walletService.verifyEnoughBalance(walletToWithdraw, transaction.getAmount());

        walletToWithdraw.withdrawBalance(transaction.getAmount());
        repository.createExternal(transaction, walletToWithdraw);
    }

    @Override
    public Pages<Transaction> filter(User user,
                                     Optional<Direction> direction,
                                     Optional<Date> startDate,
                                     Optional<Date> endDate,
                                     Optional<String> counterparty,
                                     Optional<Sort> amount,
                                     Optional<Sort> date,
                                     Pageable pageable) {

        Optional<Integer> counterpartyId = Optional.empty();
        if (counterparty.isPresent()) {
            counterpartyId = checkAndSetIfPresent(counterparty);
        }
        return repository.filter(user.getId(), direction, startDate, endDate, counterpartyId, amount, date, pageable);
    }

    @Override
    public Pages<Transaction> employeeFilter(User userExecuting,
                                             String username,
                                             Optional<String> counterparty,
                                             Optional<Direction> direction,
                                             Optional<Date> startDate,
                                             Optional<Date> endDate,
                                             Optional<Sort> amount,
                                             Optional<Sort> date,
                                             Pageable pageable) {
        if (!userExecuting.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "remove", "employee"));
        }
        int userId = userRepository.getByField("username", username).getId();
        Optional<Integer> counterpartyId = checkAndSetIfPresent(counterparty);
        return repository.filter(userId, direction, startDate, endDate, counterpartyId, amount, date, pageable);
    }

    private Optional<Integer> checkAndSetIfPresent(Optional<String> searchedPersonUsername) {
        return searchedPersonUsername.map(s -> userRepository.getByField("username", s).getId());
    }

    private void verifyUserCanMakeTransactions(User user) {
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("You should verify your email, in order to make transactions!");
        }
        if (!user.isIdVerified()) {
            throw new IllegalArgumentException("You should verify your identity, in order to make transactions!");
        }
        if (user.isBlocked()) {
            throw new IllegalArgumentException("You are currently blocked, you cannot make transactions!");
        }
    }

}