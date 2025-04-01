package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class BattleStartedSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void send(User user) {
        simpMessagingTemplate.convertAndSendToUser(user.getEmail(),
                "/topic/battle-started", new HashMap<>());
    }
}
