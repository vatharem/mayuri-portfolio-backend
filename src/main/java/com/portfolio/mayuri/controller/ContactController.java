package com.portfolio.mayuri.controller;
import com.portfolio.mayuri.Entity.ContactMassege;
import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.repository.ContactMessageRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ContactMessageRepo messageRepo;

    @PostMapping("/contact")
    public String sendEmailAndSave(@RequestBody ContactRequest request) throws MessagingException {

        // Save message in Aiven MySQL
        ContactMassege message = new ContactMassege(
                request.getName(),
                request.getEmail(),
                request.getMessage()
        );
        messageRepo.save(message);

        // Email to YOU
        MimeMessage ownerMsg = mailSender.createMimeMessage();
        MimeMessageHelper ownerHelper = new MimeMessageHelper(ownerMsg, true);
        ownerHelper.setTo("vathare69@gmail.com");
        ownerHelper.setSubject("📩 New Contact from " + request.getName());
        ownerHelper.setText(
                "Name: " + request.getName() + "<br>" +
                        "Email: " + request.getEmail() + "<br>" +
                        "Message: " + request.getMessage(),
                true
        );
        mailSender.send(ownerMsg);

        // Email to USER
        MimeMessage userMsg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(userMsg, true);
        helper.setTo(request.getEmail());
        helper.setSubject("Thank You for Contacting Me!");
        helper.setText(
                "<h3>Hi " + request.getName() + ",</h3>" +
                        "<p>Thank you for contacting me! I will get back to you soon.</p>" +
                        "<p>Regards,<br><b>Mayuri</b></p>",
                true
        );
        mailSender.send(userMsg);

        return "Message saved and emails sent successfully!";
    }
}
