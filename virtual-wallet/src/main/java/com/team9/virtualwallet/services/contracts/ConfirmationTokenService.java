package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.models.User;

public interface ConfirmationTokenService {
    ConfirmationToken create(User user);
}
