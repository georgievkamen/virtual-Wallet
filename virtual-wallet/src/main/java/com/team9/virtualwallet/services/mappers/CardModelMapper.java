package com.team9.virtualwallet.services.mappers;

import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.CardDto;
import com.team9.virtualwallet.repositories.contracts.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class CardModelMapper {

    private final CardRepository repository;

    @Autowired
    public CardModelMapper(CardRepository repository) {
        this.repository = repository;
    }

    public Card fromDto(User user, CardDto cardDto) {
        Card card = new Card();

        dtoToObject(cardDto, card);
        card.setUser(user);

        return card;
    }

    public Card fromDto(CardDto cardDto, int id) {
        Card card = repository.getById(id);

        dtoToObject(cardDto, card);

        return card;
    }

    private void dtoToObject(CardDto cardDto, Card card) {
        YearMonth ym = YearMonth.parse(cardDto.getExpirationDate(), DateTimeFormatter.ofPattern("MM/yy"));
        LocalDate expirationDate = ym.atEndOfMonth();

        card.setCardNumber(cardDto.getCardNumber());
        card.setCardHolder(cardDto.getCardHolder().toUpperCase());
        card.setExpirationDate(expirationDate);
        card.setCvv(cardDto.getCvv());
    }

}
