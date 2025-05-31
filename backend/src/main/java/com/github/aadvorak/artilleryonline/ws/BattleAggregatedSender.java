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
                var sent = false;
                synchronized (aggregated) {
                    if (aggregated.isDisabled()) {
                        break;
                    }
                    if (aggregated.getUpdate() != null) {
                        sent = true;
                        sendBattleUpdate(aggregated.getUpdate());
                        aggregated.setUpdate(null);
                    }
                }
                sleep(sent ? 100 : 10);
            }
        }).start();
    }

    private void sendBattleUpdate(BattleUpdateResponse battleUpdateResponse) {
        simpMessagingTemplate.convertAndSend("/topic/battle/"
                + battleUpdateResponse.getId() + "/updates", battleUpdateResponse.serialize());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
