package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleUpdatesSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Async("sendBattleUpdatesExecutor")
    public void start(BattleUpdatesQueue battleUpdatesQueue) {
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
    }

    private void sendBattleUpdate(BattleResponse battleResponse) {
        simpMessagingTemplate.convertAndSend("/topic/battle/"
                        + battleResponse.getId(), battleResponse.serialize());
    }

    private void sendBattleStateUpdate(BattleUpdateResponse battleUpdateResponse) {
        simpMessagingTemplate.convertAndSend("/topic/battle/"
                + battleUpdateResponse.getId() + "/updates", battleUpdateResponse.serialize());
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
