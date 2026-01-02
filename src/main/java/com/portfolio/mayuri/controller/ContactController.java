package com.portfolio.mayuri.controller;

import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.entity.ContactMessage;
import com.portfolio.mayuri.repository.ContactMessageRepo;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // allow frontend access
public class ContactController {

    private final ContactMessageRepo repo;
    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.admin}")
    private String contactReceiverEmail;


    @PostConstruct
    public void printEnv() {
        System.out.println("MAIL_FROM: " + mailFrom);
        System.out.println("CONTACT_RECEIVER_EMAIL: " + contactReceiverEmail);
    }

    public ContactController(ContactMessageRepo repo, JavaMailSender mailSender) {
        this.repo = repo;
        this.mailSender = mailSender;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> sendMail(@RequestBody ContactRequest request) {

        // 1️⃣ Validate request
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
            // 2️⃣ Save message to DB
            repo.save(new ContactMessage(
                    request.getName(),
                    request.getEmail(),
                    request.getMessage()
            ));
            System.out.println("Message saved to DB for: " + request.getEmail());

            // 3️⃣ Send mail TO ADMIN
            sendEmail(
                    contactReceiverEmail,                    // admin receiver
                    "📩 New Contact from " + request.getName(),
                    "<p><b>Name:</b> " + request.getName() + "</p>" +
                            "<p><b>Email:</b> " + request.getEmail() + "</p>" +
                            "<p><b>Message:</b><br>" + request.getMessage() + "</p>"
            );
            System.out.println("Admin mail sent to: " + contactReceiverEmail);

            // 4️⃣ Send thank-you mail TO USER
            sendEmail(
                    request.getEmail(),                      // user email
                    "Thank you for contacting me!",
                    "<p>Hi " + request.getName() + ",</p>" +
                            "<p>Thanks for reaching out. I have received your message and will reply soon.</p>" +
                            "<p>Regards,<br>Mayuri</p>"
            );
            System.out.println("User mail sent to: " + request.getEmail());

            return ResponseEntity.ok("Message sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Mail sending failed: " + e.getMessage());
        }
    }

    // 🔹 HTML Email Sender
    private void sendEmail(String to, String subject, String html) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(mailFrom); // MUST be Brevo verified sender
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true = HTML

        mailSender.send(message);
    }
}
