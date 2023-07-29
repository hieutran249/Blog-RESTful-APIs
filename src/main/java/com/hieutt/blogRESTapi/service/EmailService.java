package com.hieutt.blogRESTapi.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {
    void sendEmail(String to, String subject, Map<String, Object> variables, String type) throws MessagingException;
}
