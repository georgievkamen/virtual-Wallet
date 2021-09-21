package com.team9.virtualwallet.services.mappers;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.dtos.WalletDto;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WalletModelMapper {

    private final WalletRepository repository;
    private final UserRepository userRepository;

    @Autowired
    public WalletModelMapper(WalletRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Wallet fromDto(User user, WalletDto walletDto) {
        Wallet wallet = new Wallet();

        wallet.setName(walletDto.getName());
        wallet.setBalance(BigDecimal.valueOf(0));
        wallet.setUser(userRepository.getById(user.getId()));
        wallet.setDeleted(false);

        return wallet;
    }

    public Wallet fromDto(WalletDto walletDto, int id) {
        Wallet wallet = repository.getById(id);

        wallet.setName(walletDto.getName());

        return wallet;

    }

}
