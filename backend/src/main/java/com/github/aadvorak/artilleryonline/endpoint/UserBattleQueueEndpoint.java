package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.response.UserBattleQueueResponse;
import com.github.aadvorak.artilleryonline.service.UserBattleQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles/queue")
@RequiredArgsConstructor
public class UserBattleQueueEndpoint {

    private final UserBattleQueueService userBattleQueueService;

    @PutMapping
    public UserBattleQueueResponse addUserToQueue() {
        return userBattleQueueService.addUserToQueue();
    }

    @GetMapping
    public UserBattleQueueResponse checkUserBattleQueue() {
        return userBattleQueueService.checkUserBattleQueue();
    }

    @DeleteMapping
    public void deleteUserFromQueue() {
        userBattleQueueService.removeUserFromQueue();
    }
}
