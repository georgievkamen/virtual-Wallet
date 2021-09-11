package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
import com.team9.virtualwallet.services.contracts.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService service;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TransactionRestController(TransactionService service,
                                     AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/filter")
    public List<Transaction> transaction(@RequestHeader HttpHeaders headers,
                                         @RequestParam(required = false) SortAmount sortAmount,
                                         Optional<Date> startDate,
                                         Optional<Date> endDate,
                                         Optional<Integer> senderId,
                                         Optional<Integer> recipientId,
                                         Optional<Direction> direction,
                                         Optional<SortAmount> amount,
                                         Optional<SortDate> date) {

        User user = authenticationHelper.tryGetUser(headers);

        return service.filter(user, startDate, endDate, senderId, recipientId, direction, amount, date);

    }
}
