package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.InsufficientBalanceException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.PaymentMethod;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.enums.Type;
import com.team9.virtualwallet.repositories.contracts.PaymentMethodRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import com.team9.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.team9.virtualwallet.services.utils.MessageConstants.DUPLICATE_WALLET_NAME_MESSAGE;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository repository, UserRepository userRepository, PaymentMethodRepository paymentMethodRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<Wallet> getAll(User user) {
        return repository.getAll(user);
    }

    @Override
    public Wallet getById(User user, int id) {
        if (repository.getById(id).getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException("You can only view your own wallets!");
        }

        return repository.getById(id);
    }

    @Override
    public Object getTotalBalanceByUser(User user) {
        return repository.getTotalBalanceByUser(user);
    }

    @Override
    public void create(User user, Wallet wallet) {
        verifyNotDuplicate(user, wallet);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(Type.WALLET);
        paymentMethodRepository.create(paymentMethod);

        wallet.setId(paymentMethod.getId());

        repository.create(wallet);
        userRepository.update(user);
    }

    @Override
    public void update(User user, Wallet wallet) {
        verifyOwnership(user, wallet, "You can only edit your own wallets!");
        verifyNotDuplicateUpdate(user, wallet);
        repository.update(wallet);
    }

    @Override
    public void delete(User user, int id) {
        Wallet wallet = repository.getById(id);
        verifyOwnership(user, wallet, "You can only delete your own wallets!");
        verifyNotDefaultWallet(user, wallet);

        if (wallet.getBalance().compareTo(BigDecimal.valueOf(0)) == 0) {
            repository.delete(wallet);
        } else {
            throw new IllegalArgumentException("You can only delete a wallet that doesn't have money pending!");
        }
    }

    @Override
    public void depositBalance(Wallet wallet, BigDecimal funds) {
        wallet.depositBalance(funds);
        repository.update(wallet);
    }

    @Override
    public void withdrawBalance(Wallet wallet, BigDecimal funds) {
        verifyEnoughBalance(wallet, funds);
        wallet.withdrawBalance(funds);
        repository.update(wallet);
    }

    @Override
    public void verifyEnoughBalance(Wallet wallet, BigDecimal funds) {
        if (wallet.getBalance().compareTo(funds) < 0) {
            throw new InsufficientBalanceException("You do not have enough money in the selected wallet!");
        }
    }

    @Override
    public void setDefaultWallet(User user, Wallet wallet) {
        user.setDefaultWallet(wallet);
        userRepository.update(user);
    }

    @Override
    public void verifyWalletOwnership(Transaction transaction, Wallet wallet) {
        if (transaction.getSender().getId() != wallet.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of this wallet!");
        }
    }

    @Override
    public void verifyWalletsOwnership(Transaction transaction, Wallet walletToMoveFrom, Wallet walletToMoveTo) {
        if (transaction.getSender().getId() != walletToMoveFrom.getUser().getId() || transaction.getSender().getId() != walletToMoveTo.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of these wallets!");
        }
    }

    @Override
    public Wallet createDefaultWallet(User user) {
        Wallet defaultWallet = new Wallet();
        defaultWallet.setName("Default Wallet");
        defaultWallet.setBalance(BigDecimal.valueOf(0));
        defaultWallet.setUser(user);
        create(user, defaultWallet);
        return defaultWallet;
    }

    private void verifyNotDuplicate(User user, Wallet wallet) {
        if (repository.isDuplicate(user, wallet)) {
            throw new DuplicateEntityException(DUPLICATE_WALLET_NAME_MESSAGE);
        }
    }

    private void verifyNotDuplicateUpdate(User user, Wallet wallet) {
        Wallet walletToEdit = repository.getById(wallet.getId());

        if (repository.isDuplicate(user, wallet) && !wallet.getName().equals(walletToEdit.getName())) {
            throw new DuplicateEntityException(DUPLICATE_WALLET_NAME_MESSAGE);
        }
    }

    private void verifyOwnership(User user, Wallet wallet, String message) {
        if (wallet.getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException(message);
        }
    }

    private void verifyNotDefaultWallet(User user, Wallet wallet) {
        if (user.getDefaultWallet().getId() == wallet.getId()) {
            throw new IllegalArgumentException("You can't delete your default wallet!");
        }
    }
}
