package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleUpdatesSender implements Runnable {

    private final BattleUpdatesQueue battleUpdatesQueue;

    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void startSender() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            var battle = battleUpdatesQueue.poll();
            if (battle != null) {
                simpMessagingTemplate.convertAndSend("/topic/battle/updates/" + battle.getId(),
                        mapper.map(battle, BattleResponse.class));
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
