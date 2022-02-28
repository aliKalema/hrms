package com.smartmax.hrms.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.smartmax.hrms.entity.Reminder;
import com.smartmax.hrms.entity.User;

@Service
public class WebsocketService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebsocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    public void notifyFrontend(final User user,final Reminder reminder) {
        messagingTemplate.convertAndSend("/topic/reminders", reminder);
    }
}
