package com.github.aadvorak.artilleryonline.consumer;

import com.github.aadvorak.artilleryonline.battle.BattleFactory;
import com.github.aadvorak.artilleryonline.battle.BattleRunner;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserBattleQueueConsumer implements Runnable {

    private final UserBattleQueue userBattleQueue;

    private final BattleFactory battleFactory;

    private final UserBattleMap userBattleMap;

    @EventListener(ApplicationReadyEvent.class)
    public void startConsumer() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            var firstUserKey = getUserKeyFromQueue();
            var secondUserKey = getUserKeyFromQueue();
            var battle = battleFactory.createBattle(Set.of(firstUserKey, secondUserKey));
            userBattleMap.put(firstUserKey, battle);
            userBattleMap.put(secondUserKey, battle);
            var battleRunner = new BattleRunner(battle);
            new Thread(battleRunner).start();
        }
    }

    private String getUserKeyFromQueue() {
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
