package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.CardRepository;
import com.team9.virtualwallet.services.contracts.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.team9.virtualwallet.services.utils.MessageConstants.UNAUTHORIZED_ACTION;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository repository;

    @Autowired
    public CardServiceImpl(CardRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Card> getAll(User user) {
        return repository.getAll()
                .stream()
                .filter(card -> card.getUser().getId() == user.getId())
                .collect(Collectors.toList());
    }

    @Override
    public Card getById(User user, int id) {
        if (repository.getById(id).getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "users", "view", "their own cards"));
        }
        return repository.getById(id);
    }

    @Override
    public void create(User user, Card card) {
        if (!repository.getByFieldList("cardNumber", card.getCardNumber()).isEmpty()
                && repository.getByField("cardNumber", card.getCardNumber()).getUser().getId() == user.getId()) {
            throw new DuplicateEntityException("Card", "number", String.valueOf(card.getCardNumber()));
        }
        repository.create(card);
    }

    @Override
    public void update(User user, Card card) {

        if (user.getId() != card.getUser().getId()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "users", "update", "their own cards"));
        }

        if (!repository.getByFieldList("cardNumber", card.getCardNumber()).isEmpty()
                && repository.getByField("cardNumber", card.getCardNumber()).getUser().getId() == user.getId()) {
            throw new DuplicateEntityException("Card", "number", String.valueOf(card.getCardNumber()));
        }

        repository.update(card);
    }

    //TODO HANDLE SQL EXCEPTIONS
    @Override
    public void delete(User userExecuting, int id) {
        if (repository.getById(id).getUser().getId() != userExecuting.getId()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "users", "delete", "their own cards"));
        }

        repository.delete(id);
    }

}
