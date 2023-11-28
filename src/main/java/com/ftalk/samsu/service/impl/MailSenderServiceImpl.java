package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.service.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailSenderServiceImpl implements MailSenderService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public Boolean sendEmail(String to, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);  // true indicates that it is HTML
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        javaMailSender.send(message);
        return true;
    }
}






















