package com.github.aadvorak.artilleryonline.consumer;

import com.github.aadvorak.artilleryonline.battle.BattleFactory;
import com.github.aadvorak.artilleryonline.battle.BattleRunner;
import com.github.aadvorak.artilleryonline.collection.BattleStateUpdatesQueue;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserBattleQueueConsumer implements Runnable {

    private final UserBattleQueue userBattleQueue;

    private final BattleFactory battleFactory;

    private final UserBattleMap userBattleMap;

    private final BattleUpdatesQueue battleUpdatesQueue;

    private final BattleStateUpdatesQueue battleStateUpdatesQueue;

    private final ApplicationSettings applicationSettings;

    @EventListener(ApplicationReadyEvent.class)
    public void startConsumer() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            synchronized (userBattleQueue) {
                removeTimedOutUser();
                if (userBattleQueue.size() < 2) {
                    sleep();
                    continue;
                }
                var firstNickname = getUserFromQueue();
                var secondNickname = getUserFromQueue();
                var nicknames = Set.of(firstNickname, secondNickname);
                var battle = battleFactory.createBattle(nicknames);
                userBattleMap.put(firstNickname, battle);
                userBattleMap.put(secondNickname, battle);
                var battleRunner = new BattleRunner(battle, nicknames, userBattleMap,
                        battleUpdatesQueue, battleStateUpdatesQueue, applicationSettings);
                new Thread(battleRunner).start();
                log.info("Battle started for users: {}, queue size: {}", nicknames, userBattleQueue.size());
            }
        }
    }

    private void removeTimedOutUser() {
        var nickname = userBattleQueue.pick();
        if (nickname != null) {
            var addTime = userBattleQueue.getAddTime(nickname);
            if (System.currentTimeMillis() - addTime > applicationSettings.getBattleUpdateTimeout()) {
                userBattleQueue.remove(nickname);
                log.info("removeTimedOutElement: {}, queue size: {}", nickname, userBattleQueue.size());
            }
        }
    }

    private String getUserFromQueue() {
        while (true) {
            var userKey = userBattleQueue.poll();
            if (userKey != null) {
                return userKey;
            } else {
                sleep();
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
