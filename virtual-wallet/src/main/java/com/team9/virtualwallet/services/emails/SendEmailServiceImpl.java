package com.team9.virtualwallet.services.emails;

import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@PropertySource("classpath:messages.properties")
public class SendEmailServiceImpl implements SendEmailService {

    @Value("${mail.sender}")
    private String sender;

    private final JavaMailSender javaMailSender;

    private final String mailText;

    @Autowired
    public SendEmailServiceImpl(JavaMailSender javaMailSender) throws IOException {
        this.javaMailSender = javaMailSender;
        this.mailText = Files.readString(ResourceUtils.getFile("classpath:templates/email-templates/verify-email-template.html").toPath());
    }

    @Override
    public void sendEmailConfirmation(User user, ConfirmationToken confirmationToken) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        String time = timestamp.toString();
        String html = String.format(mailText, user.getFirstName(), user.getLastName(), confirmationToken.getConfirmationToken(), time);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.setSubject("Confirm your Registration!");
            mimeMessage.setContent(html, "text/html; charset=utf-8");
        };
        sendMail(messagePreparator);
    }

    @Async
    @Override
    public void sendMail(MimeMessagePreparator email) {
        javaMailSender.send(email);
    }

}
