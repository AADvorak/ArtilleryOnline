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
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserBattleQueueConsumer implements Runnable, SmartLifecycle {

    private volatile boolean running = false;

    private final UserBattleQueue userBattleQueue;

    private final BattleService battleService;

    private final OnlineUserService onlineUserService;

    private final ApplicationSettings applicationSettings;

    private final ThreadPoolTaskExecutor battleQueueConsumerExecutor;

    @Override
    public void start() {
        running = true;
        battleQueueConsumerExecutor.execute(this);
    }

    @Override
    public void stop() {
        running = false;
        synchronized (userBattleQueue) {
            userBattleQueue.notify();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Set<UserBattleQueueElement> elements = getElementsFromQueue();
                if (!elements.isEmpty()) {
                    var participants = elements.stream()
                            .map(BattleParticipant::of)
                            .collect(Collectors.toSet());
                    if (participants.size() < 2) {
                        participants.add(new BattleParticipant());
                    }
                    createRandomBattle(participants);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private Set<UserBattleQueueElement> getElementsFromQueue() throws InterruptedException {
        synchronized (userBattleQueue) {
            while (running && userBattleQueue.isEmpty()) {
                userBattleQueue.wait();
            }
            if (userBattleQueue.size() >= 2) {
                return Set.of(userBattleQueue.poll(), userBattleQueue.poll());
            }
            var element = getSingleElementFromQueueIfAvailable();
            if (element != null) {
                return Set.of(element);
            }
            return Set.of();
        }
    }

    private UserBattleQueueElement getSingleElementFromQueueIfAvailable() throws InterruptedException {
        var element = userBattleQueue.peek();
        if (element != null) {
            var timeout = applicationSettings.getUserBattleQueueTimeout()
                    - System.currentTimeMillis() + element.getAddTime();
            if (timeout < 0 || onlineUserService.count() < 2) {
                userBattleQueue.remove(element.getUser().getId());
                return element;
            } else if (timeout > 0) {
                userBattleQueue.wait(timeout);
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
}
