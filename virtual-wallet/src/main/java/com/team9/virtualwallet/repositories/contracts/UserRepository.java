package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.User;

public interface UserRepository extends BaseRepository<User> {

    void verifyNotDuplicate(User user);

}
