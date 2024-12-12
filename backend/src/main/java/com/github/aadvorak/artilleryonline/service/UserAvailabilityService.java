package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import com.github.aadvorak.artilleryonline.collection.UserRoomMap;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAvailabilityService {

    private final UserBattleQueue userBattleQueue;

    private final UserBattleMap userBattleMap;

    private final UserRoomMap userRoomMap;

    public void checkBattleQueueAvailability(User user) {
        checkUserNotInBattle(user);
        checkUserNotInRoom(user);
    }

    public void checkRoomAvailability(User user) {
        checkUserNotInBattleQueue(user);
    }

    public void checkTestDriveAvailability(User user) {
        checkUserNotInBattle(user);
        checkUserNotInBattleQueue(user);
        checkUserNotInRoom(user);
    }

    public void checkRoomBattleAvailability(User user) {
        checkUserNotInBattle(user);
        checkUserNotInBattleQueue(user);
    }

    private void checkUserNotInRoom(User user) {
        if (userRoomMap.get(user.getId()) != null) {
            throw new ConflictAppException("User " + user.getNickname() + " is already in room",
                    new Locale()
                            .setCode(LocaleCode.USER_ALREADY_IN_ROOM)
                            .setParams(Map.of("nickname", user.getNickname())));
        }
    }

    private void checkUserNotInBattleQueue(User user) {
        if (userBattleQueue.getByUserId(user.getId()) != null) {
            throw new ConflictAppException("User " + user.getNickname() + " is already in battle queue",
                    new Locale()
                            .setCode(LocaleCode.USER_ALREADY_IN_BATTLE_QUEUE)
                            .setParams(Map.of("nickname", user.getNickname())));
        }
    }

    private void checkUserNotInBattle(User user) {
        if (userBattleMap.get(user.getId()) != null) {
            throw new ConflictAppException("User " + user.getNickname() + " is already in battle",
                    new Locale()
                            .setCode(LocaleCode.USER_ALREADY_IN_BATTLE)
                            .setParams(Map.of("nickname", user.getNickname())));
        }
    }
}
