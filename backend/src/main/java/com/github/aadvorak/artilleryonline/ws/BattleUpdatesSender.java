package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleUpdateResponse;
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
                    if (queueElement instanceof BattleUpdateResponse battleUpdateResponse) {
                        sendBattleStateUpdate(battleUpdateResponse);
                    }
                } else {
                    sleep();
                }
            }
        }).start();
    }

    private void sendBattleUpdate(BattleResponse battleResponse) {
        simpMessagingTemplate.convertAndSend("/topic/battle/"
                        + battleResponse.getId(), battleResponse);
    }

    private void sendBattleStateUpdate(BattleUpdateResponse battleUpdateResponse) {
        simpMessagingTemplate.convertAndSend("/topic/battle/"
                + battleUpdateResponse.getId() + "/updates", battleUpdateResponse);
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
