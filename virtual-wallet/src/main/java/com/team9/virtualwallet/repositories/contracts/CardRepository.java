package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.User;

import java.util.List;

public interface CardRepository extends BaseRepository<Card> {

    List<Card> getAll(User user);
}
