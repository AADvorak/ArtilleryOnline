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

    public void addUserToQueue(String userKey) {
        if (userBattleMap.get(userKey) != null) {
            return;
        }
        userBattleQueue.add(userKey);
    }
}
