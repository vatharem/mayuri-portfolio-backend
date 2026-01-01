package com.portfolio.mayuri.controller;

import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.entity.ContactMessage;
import com.portfolio.mayuri.repository.ContactMessageRepo;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ContactController {

    private final ContactMessageRepo repo;
    private final JavaMailSender mailSender;

    @Value("${MAIL_FROM}")
    private String fromEmail;

    public ContactController(ContactMessageRepo repo, JavaMailSender mailSender) {
        this.repo = repo;
        this.mailSender = mailSender;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> sendMail(@RequestBody ContactRequest request) {

        try {
            // 1️⃣ Save message to DB
            repo.save(new ContactMessage(
                    request.getName(),
                    request.getEmail(),
                    request.getMessage()
            ));

            // 2️⃣ Mail to YOU (Admin)
            sendEmail(
                    fromEmail,
                    "📩 New Contact from " + request.getName(),
                    "<p><b>Name:</b> " + request.getName() + "</p>" +
                            "<p><b>Email:</b> " + request.getEmail() + "</p>" +
                            "<p><b>Message:</b><br>" + request.getMessage() + "</p>"
            );

            // 3️⃣ Thank-you mail to USER
            sendEmail(
                    request.getEmail(),
                    "Thank you for contacting me!",
                    "<p>Hi " + request.getName() + ",</p>" +
                            "<p>Thanks for reaching out. I have received your message and will reply soon.</p>" +
                            "<p>Regards,<br>Mayuri</p>"
            );

            return ResponseEntity.ok("Message sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Mail sending failed");
        }
    }

    // 🔹 HTML Email Sender using Brevo SMTP
    private void sendEmail(String to, String subject, String html) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);   // MUST be Brevo verified sender
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);  // true = HTML

        mailSender.send(message);
    }
}
