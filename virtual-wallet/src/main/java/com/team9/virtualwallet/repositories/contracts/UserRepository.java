package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    List<User> getByUserName(String userName);

    List<User> getByEmail(String email);

    List<User> getByPhoneNumber(String phoneNumber);

    public List<User> filter(Optional<String> userName, Optional<String> phoneNumber, Optional<String> email);

}
