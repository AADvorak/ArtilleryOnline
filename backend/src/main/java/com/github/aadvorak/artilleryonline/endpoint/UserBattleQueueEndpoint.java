package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.service.UserBattleQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/battles/queue")
@RequiredArgsConstructor
public class UserBattleQueueEndpoint {

    private final UserBattleQueueService userBattleQueueService;

    @PutMapping
    public void addUserToQueue(@CookieValue("UserKey") String userKey) {
        userBattleQueueService.addUserToQueue(userKey);
    }
}
