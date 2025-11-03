// java
package io.javabrains.demo2.service;

import com.sendgrid.SendGrid;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.Method;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private final SendGrid sendGrid;
    private final String fromAddress;
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(@Value("${SENDGRID_API_KEY}") String apiKey,
                        @Value("${app.mail.from}") String fromAddress) {
        String trimmed = apiKey == null ? "" : apiKey.trim();
        this.fromAddress = fromAddress == null ? "no-reply@example.com" : fromAddress;
        if (!trimmed.isEmpty()) {
            this.sendGrid = new SendGrid(trimmed);
            logger.info("SendGrid initialized");
        } else {
            this.sendGrid = null;
            logger.warn("SENDGRID_API_KEY not configured; emails will be skipped");
        }
    }

    public void sendWelcomeEmail(String to, String name) {
        if (sendGrid == null) {
            logger.warn("Skipping email send to {} because SendGrid is not configured", to);
            return;
        }

        Email from = new Email(fromAddress);
        Email toEmail = new Email(to);
        String subject = "Welcome to the Smokies Trip!";

        String text = "Hi " + name + ",\n\n"
                + "Thank you for registering for the Smokies trip!\n\n"
                + "Hello, here are the details for Saturday's trip. Open Google and explore the places listed below:\n"
                + "1. Newfound Gap Overlook\n"
                + "2. Clingmans Dome - if you're adventurous, we can go to the loop; otherwise, just parking is already great.\n"
                + "3. Cades Cove Loop\n"
                + "4. Laurel Falls or Cataract Falls\n"
                + "5. Morton Overlook (Sunset Point)\n\n"
                + "Sunday is Anakeesta theme park from morning to 3PM. Afterwards, return back to home!\n\n"
                + "See you soon!\n";

        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            logger.info("SendGrid response: status={}, body={}", response.getStatusCode(), response.getBody());
        } catch (IOException e) {
            logger.error("Failed to send email via SendGrid", e);
        }
    }
}