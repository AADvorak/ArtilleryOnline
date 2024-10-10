package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBattleQueueService {

    private final UserBattleQueue userBattleQueue;

    private final UserBattleMap userBattleMap;

    private final UserService userService;

    public void addUserToQueue() {
        var nickname = userService.getUserFromContext().getNickname();
        if (userBattleMap.get(nickname) != null) {
            return;
        }
        userBattleQueue.add(nickname);
    }
}
