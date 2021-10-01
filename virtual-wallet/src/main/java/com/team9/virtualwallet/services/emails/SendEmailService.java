package com.team9.virtualwallet.services.emails;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;

public interface SendEmailService {
    void sendEmailConfirmation(User user, Optional<String> invitationTokenUUID);

    void sendEmailInvitation(User invitingUser, String recipientEmail);

    void sendEmailTransactionVerification(Transaction transaction);

    @Async
    void sendMail(MimeMessagePreparator email);
}
