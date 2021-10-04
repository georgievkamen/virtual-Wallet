package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.PaymentMethod;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Type;
import com.team9.virtualwallet.repositories.contracts.CardRepository;
import com.team9.virtualwallet.repositories.contracts.PaymentMethodRepository;
import com.team9.virtualwallet.services.contracts.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.team9.virtualwallet.services.utils.Helpers.validateCardExpiryDate;
import static com.team9.virtualwallet.services.utils.MessageConstants.DUPLICATE_CARD_NUMBER_MESSAGE;
import static com.team9.virtualwallet.services.utils.MessageConstants.UNAUTHORIZED_ACTION;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository repository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public CardServiceImpl(CardRepository repository, PaymentMethodRepository paymentMethodRepository) {
        this.repository = repository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<Card> getAll(User user) {
        return repository.getAll(user);
    }

    @Override
    public Card getById(User user, int id) {
        Card card = repository.getById(id);
        if (card.getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "users", "view", "their own cards"));
        }
        return card;
    }

    @Override
    public void create(Card card) {
        verifyUnique(card);
        validateCardExpiryDate(card);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(Type.CARD);
        paymentMethodRepository.create(paymentMethod);

        card.setId(paymentMethod.getId());
        repository.create(card);
    }

    @Override
    public void update(User user, Card card) {
        if (user.getId() != card.getUser().getId()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "users", "update", "their own cards"));
        }

        verifyUniqueUpdate(card);
        validateCardExpiryDate(card);

        repository.update(card);
    }

    @Override
    public void delete(User userExecuting, int id) {
        Card card = repository.getById(id);
        if (card.getUser().getId() != userExecuting.getId()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "users", "delete", "their own cards"));
        }

        repository.delete(card);
    }

    @Override
    public void verifyCardOwnership(Transaction transaction, Card card) {
        if (transaction.getRecipient().getId() != card.getUser().getId()) {
            throw new IllegalArgumentException("You are not the owner of this card!");
        }
    }

    private void verifyUnique(Card card) {
        if (repository.isDuplicate(card)) {
            throw new DuplicateEntityException(DUPLICATE_CARD_NUMBER_MESSAGE);
        }
    }

    private void verifyUniqueUpdate(Card card) {
        Card cardToEdit = repository.getById(card.getId());

        if (repository.isDuplicate(card) && !Objects.equals(card.getCardNumber(), cardToEdit.getCardNumber())) {
            throw new DuplicateEntityException(DUPLICATE_CARD_NUMBER_MESSAGE);
        }
    }

}
