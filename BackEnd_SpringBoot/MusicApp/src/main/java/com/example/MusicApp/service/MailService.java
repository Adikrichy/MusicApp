package com.example.MusicApp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // ✅ For account verification using link
    public void send(String to, String subject, String username, String link) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("link", link);

            String htmlContent = templateEngine.process("email-template", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("adil.erzhanoc.70@gmail.com");

            mailSender.send(message);
            System.out.println("Verification email sent to " + to);

        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send verification email", e);
        }
    }

    // ✅ For password reset using a code
    public void sendResetCodeEmail(String to, String subject, String username, String code) {
        try {
            System.out.println("Sending reset code to: " + to);
            System.out.println("Code: " + code);

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("code", code);

            String htmlContent = templateEngine.process("email-reset-code", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("adil.erzhanoc.70@gmail.com");

            mailSender.send(message);
            System.out.println("Reset code email sent to " + to);

        } catch (MessagingException e) {
            System.out.println("Failed to send reset code email: " + e.getMessage());
            throw new IllegalStateException("Failed to send reset code email", e);
        }
    }
}