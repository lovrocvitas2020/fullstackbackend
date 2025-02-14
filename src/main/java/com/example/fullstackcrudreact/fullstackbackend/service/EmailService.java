package com.example.fullstackcrudreact.fullstackbackend.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

     private final JavaMailSender mailSender;

      public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(String to, String token) throws MessagingException {


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        System.out.println("sendResetPasswordEmail - > Send mail to: "+to);

        helper.setFrom("testselenium791@yahoo.com");
        helper.setTo(to);
        helper.setSubject("Reset Your Password");
        helper.setText("Click the link below to reset your password:\n" +
                "http://localhost:3000/reset-password?token=" + token, true);

        mailSender.send(message);
    }



}
