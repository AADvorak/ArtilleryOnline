package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueueElement;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueueParams;
import com.github.aadvorak.artilleryonline.dto.response.UserBattleQueueResponse;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
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

    public UserBattleQueueResponse addUserToQueue(UserBattleQueueParams params) {
        var user = userService.getUserFromContext();
        if (userBattleMap.get(user.getNickname()) != null) {
            return new UserBattleQueueResponse();
        }
        var element = new UserBattleQueueElement()
                .setUser(user)
                .setParams(params);
        userBattleQueue.add(element);
        log.info("addUserToQueue: {}, queue size: {}", user.getNickname(), userBattleQueue.size());
        return createUserBattleQueueResponse(element);
    }

    public UserBattleQueueResponse checkUserBattleQueue() {
        var user = userService.getUserFromContext();
        log.info("checkUserBattleQueue: {}, queue size: {}", user.getNickname(), userBattleQueue.size());
        var element = userBattleQueue.pick();
        if (element == null) {
            throw new NotFoundAppException();
        }
        return createUserBattleQueueResponse(element);
    }

    public void removeUserFromQueue() {
        var user = userService.getUserFromContext();
        userBattleQueue.remove(user.getId());
        log.info("removeUserFromQueue: {}, queue size: {}", user.getNickname(), userBattleQueue.size());
    }

    private UserBattleQueueResponse createUserBattleQueueResponse(UserBattleQueueElement element) {
        return new UserBattleQueueResponse()
                .setAddTime(
                        element.getAddTime() != null
                                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(element.getAddTime()), ZoneId.systemDefault())
                                : null)
                .setParams(element.getParams());
    }
}
