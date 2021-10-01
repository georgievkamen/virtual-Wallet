package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.TransactionVerificationToken;

public interface TransactionVerificationTokenService {
    TransactionVerificationToken create(Transaction transaction);
}
