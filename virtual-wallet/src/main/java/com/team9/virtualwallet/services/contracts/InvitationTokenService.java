package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.InvitationToken;
import com.team9.virtualwallet.models.User;

public interface InvitationTokenService {
    InvitationToken create(User user, String email);
}
