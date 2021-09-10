package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import com.team9.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;

    @Autowired
    public WalletServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Wallet> getAll(User user) {
        return repository.getAll(user);
    }

    @Override
    public void create(User user, Wallet wallet) {
        verifyNotDuplicate(user, wallet);
        repository.create(wallet);
    }

    @Override
    public void update(User user, Wallet wallet) {
        if (wallet.getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException("You can only edit your own wallets!");
        }
        repository.update(wallet);
    }

    @Override
    public void delete(User user, int id) {
        Wallet wallet = repository.getById(id);
        if (wallet.getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException("You can only edit your own wallets!");
        }
        repository.delete(wallet);
    }

}
