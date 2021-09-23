package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.CardDto;
import com.team9.virtualwallet.services.contracts.CardService;
import com.team9.virtualwallet.services.mappers.CardModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.team9.virtualwallet.configs.RestResponseEntityExceptionHandler.checkFields;

@RestController
@RequestMapping("/api/cards")
public class CardRestController {

    private final AuthenticationHelper authenticationHelper;
    private final CardService service;
    private final CardModelMapper modelMapper;

    public CardRestController(AuthenticationHelper authenticationHelper, CardService service, CardModelMapper modelMapper) {
        this.authenticationHelper = authenticationHelper;
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<Card> getAll(@RequestHeader HttpHeaders headers) {
        User user = authenticationHelper.tryGetUser(headers);

        return service.getAll(user);
    }

    @GetMapping("/{id}")
    public Card getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);

        return service.getById(user, id);
    }

    @PostMapping
    public Card create(@RequestHeader HttpHeaders headers, @RequestBody @Valid CardDto cardDto, BindingResult result) {
        checkFields(result);

        User user = authenticationHelper.tryGetUser(headers);

        Card card = modelMapper.fromDto(user, cardDto);
        service.create(card);

        return card;
    }

    @PutMapping("/{id}")
    public Card update(@RequestHeader HttpHeaders headers, @RequestBody @Valid CardDto cardDto, @PathVariable int id, BindingResult result) {
        checkFields(result);

        User user = authenticationHelper.tryGetUser(headers);

        Card card = modelMapper.fromDto(cardDto, id);
        service.update(user, card);

        return card;
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);

        service.delete(user, id);
    }
}
