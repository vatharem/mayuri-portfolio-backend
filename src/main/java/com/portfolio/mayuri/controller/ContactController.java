package com.portfolio.mayuri.controller;

import com.portfolio.mayuri.Service.BrevoEmailService;
import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.entity.ContactMessage;
import com.portfolio.mayuri.repository.ContactMessageRepo;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
 // allow frontend access
public class ContactController {

    private final ContactMessageRepo repo;
    private final BrevoEmailService brevoEmailService;

    @Value("${mail.admin}")
    private String contactReceiverEmail;

    public ContactController(
            ContactMessageRepo repo,
            BrevoEmailService brevoEmailService
    ) {
        this.repo = repo;
        this.brevoEmailService = brevoEmailService;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> sendMail(@RequestBody ContactRequest request) {

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required");
        }
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }

        try {
            // 1️⃣ Save to DB
            repo.save(new ContactMessage(
                    request.getName(),
                    request.getEmail(),
                    request.getMessage()
            ));

            // 2️⃣ Mail to ADMIN
            brevoEmailService.sendEmail(
                    contactReceiverEmail,
                    "📩 New Contact from " + request.getName(),
                    "<p><b>Name:</b> " + request.getName() + "</p>" +
                            "<p><b>Email:</b> " + request.getEmail() + "</p>" +
                            "<p><b>Message:</b><br>" + request.getMessage() + "</p>"
            );

            // 3️⃣ Thank-you mail to USER
            brevoEmailService.sendEmail(
                    request.getEmail(),
                    "Thank you for contacting me!",
                    "<p>Hi " + request.getName() + ",</p>" +
                            "<p>Thanks for reaching out. I’ll get back to you soon.</p>" +
                            "<p>Regards,<br>Mayuri</p>"
            );

            return ResponseEntity.ok("Message sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Mail sending failed");
        }
    }
}
