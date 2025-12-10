package com.portfolio.mayuri.controller;

import com.portfolio.mayuri.entity.ContactMessage;
import com.portfolio.mayuri.dto.ContactRequest;
import com.portfolio.mayuri.repository.ContactMessageRepo;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ContactController {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;   // your verified sender email

    private final ContactMessageRepo messageRepo;

    public ContactController(ContactMessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    @PostMapping("/contact")
    public String sendEmailAndSave(@RequestBody ContactRequest request) {

        try {
            // 1️⃣ Save into database
            ContactMessage message = new ContactMessage(
                    request.getName(),
                    request.getEmail(),
                    request.getMessage()
            );
            messageRepo.save(message);

            // 2️⃣ Email to Admin (You)
            sendMail(
                    fromEmail,
                    "vathare69@gmail.com",
                    "📩 New Contact from " + request.getName(),
                    "<h3>New Contact Message</h3>" +
                            "<p><b>Name:</b> " + request.getName() + "</p>" +
                            "<p><b>Email:</b> " + request.getEmail() + "</p>" +
                            "<p><b>Message:</b> " + request.getMessage() + "</p>"
            );

            // 3️⃣ Email to User (Thank you message)
            sendMail(
                    fromEmail,
                    request.getEmail(),
                    "Thank You for Contacting Me!",
                    "<h3>Hi " + request.getName() + ",</h3>" +
                            "<p>Thank you for contacting me! I will get back to you soon.</p>" +
                            "<p>Regards,<br><b>Mayuri</b></p>"
            );

            return "Message saved and emails sent successfully!";

        } catch (IOException e) {
            e.printStackTrace();
            return "Message saved but failed to send emails.";
        }
    }

    // Helper method to send email
    private void sendMail(String from, String to, String subject, String htmlBody) throws IOException {
        Email fromEmailObj = new Email(from);
        Email toEmailObj = new Email(to);

        // HTML content
        Content contentHtml = new Content("text/html", htmlBody);

        // Plain text content (for better delivery)
        String plainText = htmlBody.replaceAll("<[^>]*>", "");
        Content contentText = new Content("text/plain", plainText);

        // Mail object
        Mail mail = new Mail();
        mail.setFrom(fromEmailObj);
        mail.setSubject(subject);

        // Personalization
        Personalization personalization = new Personalization();
        personalization.addTo(toEmailObj);
        mail.addPersonalization(personalization);

        // Add contents
        mail.addContent(contentHtml);
        mail.addContent(contentText);

        // Send
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);

        // Debugging
        System.out.println("Email sent to: " + to);
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
        System.out.println("Headers: " + response.getHeaders());
    }
}
