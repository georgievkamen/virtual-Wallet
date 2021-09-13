package com.team9.virtualwallet.services.emails;

import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:messages.properties")
public class SendEmailServiceImpl implements SendEmailService {

    @Value("${mail.sender}")
    private String sender;

    private JavaMailSender javaMailSender;

    @Autowired
    public SendEmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmailConfirmation(User user, ConfirmationToken confirmationToken) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setSubject("Confirm your Registration!");
        mail.setFrom(sender);
        mail.setText(String.format("You have created an account on DeliverIT!" +
                "We are very happy to welcome you!" +
                "The last step is to verify your account via the link below:" +
                "http://localhost/confirm-account?token=%s", confirmationToken.getConfirmationToken()));

        sendMail(mail);
    }

    @Async
    @Override
    public void sendMail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }
}
