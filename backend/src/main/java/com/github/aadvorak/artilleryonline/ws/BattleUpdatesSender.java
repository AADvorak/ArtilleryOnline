package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleUpdatesSender {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void start(BattleUpdatesQueue battleUpdatesQueue) {
        new Thread(() -> {
            while (true) {
                var battleResponse = battleUpdatesQueue.poll();
                if (battleResponse != null) {
                    if (battleResponse.getId() == null) {
                        break;
                    }
                    simpMessagingTemplate.convertAndSend("/topic/battle/updates/" + battleResponse.getId(),
                            battleResponse);
                } else {
                    sleep();
                }
            }
        }).start();
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
