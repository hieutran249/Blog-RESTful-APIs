package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;
//
    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine thymeleafTemplateEngine) {
        this.mailSender = mailSender;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @Override
    public void sendEmail(String to, String subject, Map<String, Object> variables, String type) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(new InternetAddress("hieutt.tnvn@gmail.com"));
        helper.setTo(new InternetAddress(to));
        helper.setSubject(subject);
        String htmlBody = getEmailContent(variables, type);
        helper.setText(htmlBody, true);

        mailSender.send(message);
        System.out.println("mail sent");
    }

    private String getEmailContent(Map<String, Object> variables, String type) throws MessagingException {
        Context thymeleafContext = new Context();
        // if there are more many variables -> setVariables(Map)
        // set the variables in template with variables
        thymeleafContext.setVariables(variables);
        String htmlBody;
        if (type.equalsIgnoreCase("welcome")) {
            htmlBody = thymeleafTemplateEngine.process("welcome.html", thymeleafContext);
        }
        else htmlBody = thymeleafTemplateEngine.process("forgot-password.html", thymeleafContext);

        return htmlBody;
    }

}
