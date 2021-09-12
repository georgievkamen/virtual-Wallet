package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.dtos.WalletDto;
import com.team9.virtualwallet.services.contracts.WalletService;
import com.team9.virtualwallet.services.mappers.WalletModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users/wallets")
public class WalletRestController {

    private final AuthenticationHelper authenticationHelper;
    private final WalletService service;
    private final WalletModelMapper modelMapper;

    @Autowired
    public WalletRestController(AuthenticationHelper authenticationHelper, WalletService service, WalletModelMapper modelMapper) {
        this.authenticationHelper = authenticationHelper;
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<Wallet> getAll(@RequestHeader HttpHeaders headers) {
        User user = authenticationHelper.tryGetUser(headers);
        return service.getAll(user);
    }

    @GetMapping("/{id}")
    public Wallet getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        return service.getById(user, id);
    }

    @PostMapping
    public Wallet create(@RequestHeader HttpHeaders headers, @Valid @RequestBody WalletDto walletDto) {
        User user = authenticationHelper.tryGetUser(headers);
        Wallet wallet = modelMapper.fromDto(user, walletDto);
        service.create(user, wallet);
        return wallet;
    }

    @PutMapping("/{id}")
    public Wallet update(@RequestHeader HttpHeaders headers, @Valid @RequestBody WalletDto walletDto, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        Wallet wallet = modelMapper.fromDto(walletDto, id);
        service.update(user, wallet);
        return wallet;
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        service.delete(user, id);
    }

}
