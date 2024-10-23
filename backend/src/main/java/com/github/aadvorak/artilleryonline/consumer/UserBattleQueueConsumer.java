package com.github.aadvorak.artilleryonline.consumer;

import com.github.aadvorak.artilleryonline.battle.BattleFactory;
import com.github.aadvorak.artilleryonline.battle.BattleRunner;
import com.github.aadvorak.artilleryonline.collection.*;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

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
            var elements = getElementsFromQueue();
            if (elements.isEmpty()) {
                sleep();
                continue;
            }
            var nicknames = elements.stream()
                    .map(element -> element.getUser().getNickname())
                    .collect(Collectors.toSet());
            var battle = battleFactory.createBattle(elements);
            elements.forEach(element -> userBattleMap.put(element.getUser().getId(), battle));
            var battleRunner = new BattleRunner(battle, nicknames, userBattleMap,
                    battleUpdatesQueue, battleStateUpdatesQueue, applicationSettings);
            new Thread(battleRunner).start();
            log.info("Battle started for users: {}, queue size: {}", nicknames, userBattleQueue.size());
        }
    }

    private Set<UserBattleQueueElement> getElementsFromQueue() {
        synchronized (userBattleQueue) {
            removeTimedOutUser();
            if (userBattleQueue.size() < 2) {
                return Set.of();
            }
            return Set.of(getElementFromQueue(), getElementFromQueue());
        }
    }

    private void removeTimedOutUser() {
        var element = userBattleQueue.pick();
        if (element != null) {
            var addTime = element.getAddTime();
            if (System.currentTimeMillis() - addTime > applicationSettings.getUserBattleQueueTimeout()) {
                userBattleQueue.remove(element.getUser().getId());
                log.info("removeTimedOutUser: {}, queue size: {}",
                        element.getUser().getNickname(), userBattleQueue.size());
            }
        }
    }

    private UserBattleQueueElement getElementFromQueue() {
        while (true) {
            var element = userBattleQueue.poll();
            if (element != null) {
                return element;
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
