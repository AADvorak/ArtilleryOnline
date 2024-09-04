package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.service.UserBattleQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// todo remove
@CrossOrigin(origins = "http://localhost:5173/", maxAge = 3600)
@RestController
@RequestMapping("/api/battles/queue")
@RequiredArgsConstructor
public class UserBattleQueueEndpoint {

    private final UserBattleQueueService userBattleQueueService;

    @PutMapping
    public void addUserToQueue(@RequestHeader("UserKey") String userKey) {
        userBattleQueueService.addUserToQueue(userKey);
    }
}
