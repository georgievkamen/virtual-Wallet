package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.ExternalTransactionDto;
import com.team9.virtualwallet.models.dtos.TransactionDto;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
import com.team9.virtualwallet.services.contracts.TransactionService;
import com.team9.virtualwallet.services.mappers.TransactionModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService service;
    private final AuthenticationHelper authenticationHelper;
    private final TransactionModelMapper modelMapper;

    @Autowired
    public TransactionRestController(TransactionService service,
                                     AuthenticationHelper authenticationHelper, TransactionModelMapper modelMapper) {
        this.service = service;
        this.authenticationHelper = authenticationHelper;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public Transaction create(@RequestHeader HttpHeaders headers, @RequestBody @Valid TransactionDto transactionDto, @RequestParam(required = false) Optional<Integer> categoryId) {
        User user = authenticationHelper.tryGetUser(headers);

        //TODO Do we need create a transaction by username//email//phone number
        Transaction transaction = modelMapper.fromDto(user, transactionDto);
        service.create(transaction, transactionDto.getSelectedWalletId(), categoryId);

        return transaction;
    }

    @PostMapping("/external/deposit")
    public Transaction createExternalDeposit(@RequestHeader HttpHeaders headers, @RequestBody @Valid ExternalTransactionDto externalTransactionDto, @RequestParam(required = false) Optional<Integer> categoryId) {
        User user = authenticationHelper.tryGetUser(headers);

        RestTemplate restTemplate = new RestTemplate();

        boolean rejected = Boolean.TRUE.equals(restTemplate.getForObject("http://localhost/api/dummy", Boolean.class));

        //TODO Do we need create a transaction by username//email//phone number
        Transaction transaction = modelMapper.fromExternalDto(user, externalTransactionDto);
        service.createExternalDeposit(transaction, externalTransactionDto.getSelectedWalletId(),
                externalTransactionDto.getSelectedCardId(), rejected, categoryId);

        return transaction;
    }

    @PostMapping("/external/withdraw")
    public Transaction createExternalWithdraw(@RequestHeader HttpHeaders headers, @RequestBody @Valid ExternalTransactionDto externalTransactionDto, @RequestParam(required = false) Optional<Integer> categoryId) {
        User user = authenticationHelper.tryGetUser(headers);

        //TODO Do we need create a transaction by username//email//phone number
        Transaction transaction = modelMapper.fromExternalDto(user, externalTransactionDto);
        service.createExternalWithdraw(transaction, externalTransactionDto.getSelectedWalletId(),
                externalTransactionDto.getSelectedCardId(), categoryId);

        return transaction;
    }

    @GetMapping("/filter")
    public List<Transaction> filter(@RequestHeader HttpHeaders headers,
                                    @RequestParam(required = false)
                                            Optional<Date> startDate,
                                    Optional<Date> endDate,
                                    Optional<Integer> categoryId,
                                    Optional<Integer> senderId,
                                    Optional<Integer> recipientId,
                                    Optional<Direction> direction,
                                    Optional<SortAmount> amount,
                                    Optional<SortDate> date) {

        User user = authenticationHelper.tryGetUser(headers);

        return service.filter(user, startDate, endDate, categoryId, senderId, recipientId, direction, amount, date);
    }

    @GetMapping
    public List<Transaction> getAll(@RequestHeader HttpHeaders headers) {
        User user = authenticationHelper.tryGetUser(headers);

        return service.getAll(user);
    }

    @GetMapping("/{id}")
    public Transaction getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);

        return service.getById(user, id);
    }

}
