package com.portfolio.mayuri.controller;


import com.portfolio.mayuri.entity.ContactMessage;
import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.repository.ContactMessageRepo;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;


@RestController
@RequestMapping("/api")
public class ContactController {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;   // your verified single sender email

    private final ContactMessageRepo messageRepo;

    public ContactController(ContactMessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    @PostMapping("/contact")
    public String sendEmailAndSave(@RequestBody ContactRequest request) throws IOException {

        // 1️⃣ Save into database
        ContactMessage message = new ContactMessage(
                request.getName(),
                request.getEmail(),
                request.getMessage()
        );
        messageRepo.save(message);

        // 2️⃣ Email to YOU (notification)
        sendMail(
                fromEmail,
                "vathare69@gmail.com",
                "📩 New Contact from " + request.getName(),
                "Name: " + request.getName() + "<br>" +
                        "Email: " + request.getEmail() + "<br>" +
                        "Message: " + request.getMessage()
        );

        // 3️⃣ Email to USER (thank you message)
        sendMail(
                fromEmail,
                request.getEmail(),
                "Thank You for Contacting Me!",
                "<h3>Hi " + request.getName() + ",</h3>" +
                        "<p>Thank you for contacting me! I will get back to you soon.</p>" +
                        "<p>Regards,<br><b>Mayuri</b></p>"
        );

        return "Message saved and emails sent successfully!";
    }

    //  Helper method to send email using SendGrid
    private void sendMail(String from, String to, String subject, String body) throws IOException {
        Email fromEmailObj = new Email(from);
        Email toEmailObj = new Email(to);

        Content content = new Content("text/html", body);
        Mail mail = new Mail(fromEmailObj, subject, toEmailObj, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request req = new Request();

        req.setMethod(Method.POST);
        req.setEndpoint("mail/send");
        req.setBody(mail.build());

        sg.api(req); // send email
    }
}
