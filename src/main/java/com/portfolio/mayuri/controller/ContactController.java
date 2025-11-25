/*
package com.portfolio.mayuri.controller;

import com.portfolio.mayuri.Entity.ContactMassege;
import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.repository.ContactMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ContactMessageRepo messageRepo;

    @PostMapping("/contact")
    public String sendEmailAndSave(@RequestBody ContactRequest request) {
        try {
            // Save message in database
            ContactMassege message = new ContactMassege(
                    request.getName(),
                    request.getEmail(),
                    request.getMessage()
            );
            messageRepo.save(message);

            // Email to you (owner)
            SimpleMailMessage ownerMail = new SimpleMailMessage();
            ownerMail.setTo("vathare69@gmail.com");
            ownerMail.setSubject("New Contact from " + request.getName());
            ownerMail.setText(
                    "Name: " + request.getName() +
                            "\nEmail: " + request.getEmail() +
                            "\nMessage: " + request.getMessage()
            );
            mailSender.send(ownerMail);

            // Thank-you email to user
            SimpleMailMessage userMail = new SimpleMailMessage();
            userMail.setTo(request.getEmail());
            userMail.setSubject("Thank You for Contacting Me!");
            userMail.setText(
                    "Hi " + request.getName() + ",\n\n" +
                            "Thanks for reaching out! I’ll get back to you soon.\n\n" +
                            "Best,\nMayuri"
            );
            mailSender.send(userMail);

            return "✅ Message saved and emails sent successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send message: " + e.getMessage();
        }
    }
}
*/
package com.portfolio.mayuri.controller;

import com.portfolio.mayuri.Entity.ContactMassege;
import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.repository.ContactMessageRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://myportfolio-frontend-pi.vercel.app",
        "https://*.vercel.app"
})

public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ContactMessageRepo messageRepo;

    @PostMapping("/contact")
    public String sendEmailAndSave(@RequestBody ContactRequest request) throws MessagingException {
        ContactMassege message = new ContactMassege(request.getName(), request.getEmail(), request.getMessage());
        messageRepo.save(message);

        // Send email to you (plain text)
        MimeMessage ownerMsg = mailSender.createMimeMessage();
        MimeMessageHelper ownerHelper = new MimeMessageHelper(ownerMsg, true);
        ownerHelper.setTo("vathare69@gmail.com");
        ownerHelper.setSubject("📩 New Contact from " + request.getName());
        ownerHelper.setText(
                "Name: " + request.getName() + "<br>" +
                        "Email: " + request.getEmail() + "<br>" +
                        "Message: " + request.getMessage(),
                true);
        mailSender.send(ownerMsg);

        // Send HTML Thank-You email to user
        MimeMessage userMsg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(userMsg, true);
        helper.setTo(request.getEmail());
        helper.setSubject("Thank You for Contacting Me, " + request.getName() + "!");
        helper.setText(
                "<div style='font-family:Arial,sans-serif;padding:20px;color:#333'>" +
                        "<h2 style='color:#007bff;'>Hi " + request.getName() + ",</h2>" +
                        "<p>Thank you for reaching out! I truly appreciate your message and will get back to you soon.</p>" +
                        "<br><p>Warm regards,<br><b>Mayuri Vathare</b><br>Full Stack Developer</p>" +
                        "<hr><p style='font-size:12px;color:#777'>This is an automated response.</p>" +
                        "</div>",
                true);
        mailSender.send(userMsg);

        return "Message saved and emails sent successfully!";
    }
}

