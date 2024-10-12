package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import com.github.aadvorak.artilleryonline.dto.response.UserBattleQueueResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBattleQueueService {

    private final UserBattleQueue userBattleQueue;

    private final UserBattleMap userBattleMap;

    private final UserService userService;

    public UserBattleQueueResponse addUserToQueue() {
        var nickname = userService.getUserFromContext().getNickname();
        if (userBattleMap.get(nickname) != null) {
            return new UserBattleQueueResponse();
        }
        userBattleQueue.add(nickname);
        log.info("addUserToQueue: {}, queue size: {}", nickname, userBattleQueue.size());
        return createUserBattleQueueResponse(nickname);
    }

    public UserBattleQueueResponse checkUserBattleQueue() {
        var nickname = userService.getUserFromContext().getNickname();
        log.info("checkUserBattleQueue: {}, queue size: {}", nickname, userBattleQueue.size());
        return createUserBattleQueueResponse(nickname);
    }

    public void removeUserFromQueue() {
        var nickname = userService.getUserFromContext().getNickname();
        userBattleQueue.remove(nickname);
        log.info("removeUserFromQueue: {}, queue size: {}", nickname, userBattleQueue.size());
    }

    private UserBattleQueueResponse createUserBattleQueueResponse(String nickname) {
        var addTime = userBattleQueue.getAddTime(nickname);
        return new UserBattleQueueResponse().setAddTime(
                addTime != null
                        ? LocalDateTime.ofInstant(Instant.ofEpochMilli(addTime), ZoneId.systemDefault())
                        : null
        );
    }
}
