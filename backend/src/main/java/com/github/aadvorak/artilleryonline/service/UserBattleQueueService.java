package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBattleQueueService {

    private final UserBattleQueue userBattleQueue;

    public void addUserToQueue(String userKey) {
        // todo if user already in the battle, do not add to queue
        userBattleQueue.add(userKey);
    }
}
