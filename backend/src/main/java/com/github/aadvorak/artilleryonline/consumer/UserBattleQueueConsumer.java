package com.github.aadvorak.artilleryonline.consumer;

import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import com.github.aadvorak.artilleryonline.battle.BattleStarter;
import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.collection.*;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.ws.BattleStartedSender;
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

    private final BattleStarter battleStarter;

    private final UserBattleMap userBattleMap;

    private final ApplicationSettings applicationSettings;

    private final BattleStartedSender battleStartedSender;

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
            synchronized (userBattleMap) {
                try {
                    battleStarter.start(elements.stream()
                            .map(BattleParticipant::of).collect(Collectors.toSet()), BattleType.RANDOM);
                    elements.stream()
                            .map(UserBattleQueueElement::getUser)
                            .forEach(battleStartedSender::send);
                    log.info("Users {} removed from queue to battle, queue size: {}",
                            nicknames, userBattleQueue.size());
                } catch (ConflictAppException ex) {
                    log.info("Users {} removed from queue but battle not started, queue size: {}",
                            nicknames, userBattleQueue.size());
                }
            }
        }
    }

    private Set<UserBattleQueueElement> getElementsFromQueue() {
        synchronized (userBattleQueue) {
            if (userBattleQueue.size() == 0) {
                waitQueue();
            }
            removeTimedOutUser();
            if (userBattleQueue.size() < 2) {
                return Set.of();
            }
            return Set.of(getElementFromQueue(), getElementFromQueue());
        }
    }

    private void removeTimedOutUser() {
        var element = userBattleQueue.peek();
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

    private void waitQueue() {
        try {
            userBattleQueue.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
