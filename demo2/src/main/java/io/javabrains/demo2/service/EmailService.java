package io.javabrains.demo2.service;

// EmailService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to the Smokies Trip!");

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

        message.setText(text);

        mailSender.send(message);
    }
}
