package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.collection.UserBattleQueueParams;
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
    public UserBattleQueueResponse addUserToQueue(@RequestBody UserBattleQueueParams params) {
        return userBattleQueueService.addUserToQueue(params);
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
