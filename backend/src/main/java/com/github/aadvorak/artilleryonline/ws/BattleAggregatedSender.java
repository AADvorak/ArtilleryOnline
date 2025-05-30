package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.battle.BattleAggregated;
import com.github.aadvorak.artilleryonline.dto.response.BattleUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleAggregatedSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void start(BattleAggregated aggregated) {
        new Thread(() -> {
            while (true) {
                synchronized (aggregated) {
                    if (aggregated.isDisabled()) {
                        break;
                    }
                    if (aggregated.getUpdate() != null) {
                        sendBattleUpdate(aggregated.getUpdate());
                        aggregated.setUpdate(null);
                    }
                }
                sleep();
            }
        }).start();
    }

    private void sendBattleUpdate(BattleUpdateResponse battleUpdateResponse) {
        simpMessagingTemplate.convertAndSend("/topic/battle/"
                + battleUpdateResponse.getId() + "/updates", battleUpdateResponse.serialize());
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
