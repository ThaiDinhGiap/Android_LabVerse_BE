package com.mss.prm_project.service;

import com.mss.prm_project.model.Mail;

public interface MailService {

    void sendHtmlMail(Mail mail);

    void sentTextMail(Mail mail);

    void sentVerifyMail(String email, String token);
    public void sendNotification(String toEmail, String text);
}
