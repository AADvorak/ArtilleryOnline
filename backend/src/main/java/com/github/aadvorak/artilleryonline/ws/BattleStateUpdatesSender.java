package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.collection.BattleStateUpdatesQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleStateUpdatesSender implements Runnable {

    private final BattleStateUpdatesQueue battleStateUpdatesQueue;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void startSender() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            var battleStateResponse = battleStateUpdatesQueue.poll();
            if (battleStateResponse != null) {
                simpMessagingTemplate.convertAndSend("/topic/battle/updates/"
                        + battleStateResponse.getId() + "/state", battleStateResponse);
            } else {
                sleep();
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
