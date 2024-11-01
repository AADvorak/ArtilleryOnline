package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.dto.response.MessageResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagesSender {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessage(User user, MessageResponse messageResponse) {
        simpMessagingTemplate.convertAndSendToUser(user.getEmail(),
                "/topic/messages", messageResponse);
    }
}
