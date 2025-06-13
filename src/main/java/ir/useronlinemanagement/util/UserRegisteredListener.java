package ir.useronlinemanagement.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserRegisteredListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public UserRegisteredListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        var user = event.getUser();
        log.info("✅ کاربر جدید ثبت شد: {}", user.getUsername());

        // ارسال پیام خوشامدگویی به کلاینت از طریق WebSocket
        messagingTemplate.convertAndSend("/topic/welcome", "👋 خوش اومدی " + user.getFirstName());
    }
}

