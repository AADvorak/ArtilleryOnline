package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.collection.UserBattleQueueParams;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
@RequiredArgsConstructor
public class UserBattleEndpoint {

    private final BattleService battleService;

    @GetMapping
    public BattleResponse getBattle() {
        return battleService.getBattle();
    }

    @GetMapping("/tracking")
    public String getBattleTracking() {
        return battleService.getBattleTracking();
    }

    @PostMapping("/test-drive")
    public BattleResponse testDrive(@RequestBody UserBattleQueueParams params) {
        return battleService.createTestDrive(params);
    }

    @DeleteMapping("/leave")
    public void leaveBattle() {
        battleService.leaveBattle();
    }
}
