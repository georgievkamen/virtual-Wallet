package com.team9.virtualwallet.services.emails;

import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.models.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

public interface SendEmailService {
    void sendEmailConfirmation(User user, ConfirmationToken confirmationToken);

    @Async
    void sendMail(SimpleMailMessage email);
}
