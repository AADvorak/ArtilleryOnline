package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.error.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorsSender {
    
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendError(User user, ErrorResponse errorResponse) {
        simpMessagingTemplate.convertAndSendToUser(user.getEmail(),
                "/topic/errors", errorResponse);
    }
}
