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
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        String time = timestamp.toString();

        StringBuilder sb = new StringBuilder();
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:templates/email-templates/verify-email-template.html");
        } catch (FileNotFoundException e) {
            System.out.println("F");
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String html = String.format(sb.toString(), user.getFirstName(), user.getLastName(), confirmationToken.getConfirmationToken(), time);

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
