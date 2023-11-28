package com.ftalk.samsu.service;

public interface MailSenderService {
    Boolean sendEmail(String to, String subject, String text);
}
