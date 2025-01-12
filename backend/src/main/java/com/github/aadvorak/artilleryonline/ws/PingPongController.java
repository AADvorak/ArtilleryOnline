package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PingPongController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserService userService;

    @MessageMapping("/ping")
    public void receivePingAndSendPong(PingPongMessage pingPongMessage) {
        var user = userService.getUserFromContext();
        simpMessagingTemplate.convertAndSendToUser(user.getEmail(),
                "/topic/pong", pingPongMessage);
    }

    @Setter
    @Getter
    public static final class PingPongMessage {
        private long timestamp;
    }
}
