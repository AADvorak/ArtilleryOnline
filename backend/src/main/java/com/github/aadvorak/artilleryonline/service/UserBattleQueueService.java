package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.BattleStarter;
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

    private final UserService userService;

    private final UserAvailabilityService userAvailabilityService;

    private final BattleStarter battleStarter;

    public UserBattleQueueResponse addUserToQueue(UserBattleQueueParams params) {
        battleStarter.checkMaxBattles();
        var user = userService.getUserFromContext();
        userAvailabilityService.checkBattleQueueAvailability(user);
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
        var element = userBattleQueue.getByUserId(user.getId());
        if (element == null) {
            throw new NotFoundAppException();
        }
        return createUserBattleQueueResponse(element);
    }

    public void removeUserFromQueue() {
        var user = userService.getUserFromContext();
        if (userBattleQueue.getByUserId(user.getId()) != null) {
            userBattleQueue.remove(user.getId());
            log.info("removeUserFromQueue: {}, queue size: {}", user.getNickname(), userBattleQueue.size());
        }
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
