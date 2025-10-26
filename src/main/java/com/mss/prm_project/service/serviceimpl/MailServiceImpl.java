package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.model.Mail;
import com.mss.prm_project.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String defaultMailFrom;


    @Override
    public void sendHtmlMail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(defaultMailFrom);
            helper.setTo(mail.getMailTo());
            helper.setSubject(mail.getMailSubject());
            helper.setText(mail.getMailContent(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sentTextMail(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getMailTo());
        message.setSubject(mail.getMailSubject());
        message.setText(mail.getMailContent());
        mailSender.send(message);
    }

    @Override
    public void sentVerifyMail(String email, String token) {
        String verifyLink = "http://localhost:8080/api/auth/email-verified?token=" + token;

        String html = """
            <h2>LAB_VERSE - VERIFY EMAIL</h2>
            <p>Please follow the below link to finish verify your account:</p>
            <a href="%s">VERIFY</a>
            <p>This link will out of date after 10 minutes. </p>
        """.formatted(verifyLink);

        Mail mail = new Mail();
        mail.setMailTo(new String[]{email});
        mail.setMailSubject("VERIFY YOUR ACCOUNT");
        mail.setMailContent(html);

        this.sendHtmlMail(mail);
    }
}
