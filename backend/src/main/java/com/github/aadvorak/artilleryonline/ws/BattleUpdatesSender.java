package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleStateResponse;
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
                var queueElement = battleUpdatesQueue.poll();
                if (queueElement != null) {
                    if (queueElement.getId() == null) {
                        break;
                    }
                    if (queueElement instanceof BattleResponse battleResponse) {
                        sendBattleUpdate(battleResponse);
                    }
                    if (queueElement instanceof BattleStateResponse battleStateResponse) {
                        sendBattleStateUpdate(battleStateResponse);
                    }
                } else {
                    sleep();
                }
            }
        }).start();
    }

    private void sendBattleUpdate(BattleResponse battleResponse) {
        simpMessagingTemplate.convertAndSend("/topic/battle/updates/"
                        + battleResponse.getId(), battleResponse);
    }

    private void sendBattleStateUpdate(BattleStateResponse battleStateResponse) {
        simpMessagingTemplate.convertAndSend("/topic/battle/updates/"
                + battleStateResponse.getId() + "/state", battleStateResponse);
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
