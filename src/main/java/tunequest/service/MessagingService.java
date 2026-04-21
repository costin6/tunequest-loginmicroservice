package tunequest.service;

import jakarta.jms.TextMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessagingService {

    private final JmsTemplate jmsTemplate;

    public MessagingService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String messageContent) {
        try {
            jmsTemplate.send("user-login-queue", session -> session.createTextMessage(messageContent));
            System.out.println("Message sent to queue: " + "user-login-queue");
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
}
