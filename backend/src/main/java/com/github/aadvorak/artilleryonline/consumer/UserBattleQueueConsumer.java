package com.github.aadvorak.artilleryonline.consumer;

import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueue;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueueElement;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.service.BattleService;
import com.github.aadvorak.artilleryonline.service.OnlineUserService;
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

    private final BattleService battleService;

    private final OnlineUserService onlineUserService;

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
            var participants = elements.stream()
                    .map(BattleParticipant::of)
                    .collect(Collectors.toSet());
            if (participants.size() < 2) {
                participants.add(new BattleParticipant());
            }
            createRandomBattle(participants);
        }
    }

    // todo refactor
    private Set<UserBattleQueueElement> getElementsFromQueue() {
        synchronized (userBattleQueue) {
            if (userBattleQueue.size() == 0) {
                waitQueue();
            }
            if (userBattleQueue.size() >= 2) {
                return Set.of(getElementFromQueue(), getElementFromQueue());
            }
            var element = getSingleElementFromQueueIfAvailable();
            if (element != null) {
                return Set.of(element);
            }
            return Set.of();
        }
    }

    private UserBattleQueueElement getSingleElementFromQueueIfAvailable() {
        var element = userBattleQueue.peek();
        if (element != null) {
            var addTime = element.getAddTime();
            if (System.currentTimeMillis() - addTime > applicationSettings.getUserBattleQueueTimeout()
                    || onlineUserService.count() < 2) {
                userBattleQueue.remove(element.getUser().getId());
                return element;
            }
        }
        return null;
    }

    private void createRandomBattle(Set<BattleParticipant> participants) {
        var nicknames = participants.stream()
                .filter(participant -> participant.getUser() != null)
                .map(participant -> participant.getUser().getNickname())
                .collect(Collectors.toSet());
        try {
            battleService.createRandomBattle(participants);
            log.info("Users {} removed from queue to battle, queue size: {}",
                    nicknames, userBattleQueue.size());
        } catch (ConflictAppException ex) {
            log.info("Users {} removed from queue but battle not started, queue size: {}",
                    nicknames, userBattleQueue.size());
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
