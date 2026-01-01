package com.portfolio.mayuri.controller;

import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.entity.ContactMessage;
import com.portfolio.mayuri.repository.ContactMessageRepo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.internet.MimeMessage;

@RestController
@RequestMapping("/api")

public class ContactController {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final ContactMessageRepo repo;
    private final JavaMailSender mailSender;

    public ContactController(ContactMessageRepo repo, JavaMailSender mailSender) {
        this.repo = repo;
        this.mailSender = mailSender;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> sendMail(@RequestBody ContactRequest request) {
        System.out.println("FROM EMAIL = " + fromEmail);
        try {
            // Save to DB
            repo.save(new ContactMessage(
                    request.getName(),
                    request.getEmail(),
                    request.getMessage()
            ));

            // Mail to Admin
            sendEmail(
                    fromEmail,
                    fromEmail,
                    "📩 New Contact from " + request.getName(),
                    "<p><b>Name:</b> " + request.getName() + "</p>" +
                            "<p><b>Email:</b> " + request.getEmail() + "</p>" +
                            "<p><b>Message:</b> " + request.getMessage() + "</p>"
            );

            // Mail to User
            sendEmail(
                    fromEmail,
                    request.getEmail(),
                    "Thank you for contacting me!",
                    "<p>Hi " + request.getName() + ",</p>" +
                            "<p>Thanks for reaching out. I will reply soon.</p>" +
                            "<p>Regards,<br>Mayuri</p>"
            );

            return ResponseEntity.ok("Message sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Mail sending failed");
        }
    }

    private void sendEmail(String replyTo, String to, String subject, String html) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setFrom(fromEmail);      // MUST be authenticated Gmail
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        helper.setReplyTo(replyTo);     // user email / admin reply

        mailSender.send(message);
    }
}
