package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import com.github.aadvorak.artilleryonline.collection.UserRoomMap;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new ConflictAppException("User " + user.getNickname() + " is already in room");
        }
    }

    private void checkUserNotInBattleQueue(User user) {
        if (userBattleQueue.getByUserId(user.getId()) != null) {
            throw new ConflictAppException("User " + user.getNickname() + " is already in battle queue");
        }
    }

    private void checkUserNotInBattle(User user) {
        if (userBattleMap.get(user.getId()) != null) {
            throw new ConflictAppException("User " + user.getNickname() + " is already in battle");
        }
    }
}
