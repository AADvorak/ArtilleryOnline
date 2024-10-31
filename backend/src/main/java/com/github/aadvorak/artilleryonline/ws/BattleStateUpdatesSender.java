package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.collection.BattleStateUpdatesQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleStateUpdatesSender {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void start(BattleStateUpdatesQueue battleStateUpdatesQueue) {
        new Thread(() -> {
            while (true) {
                var battleStateResponse = battleStateUpdatesQueue.poll();
                if (battleStateResponse != null) {
                    if (battleStateResponse.getId() == null) {
                        break;
                    }
                    simpMessagingTemplate.convertAndSend("/topic/battle/updates/"
                            + battleStateResponse.getId() + "/state", battleStateResponse);
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
