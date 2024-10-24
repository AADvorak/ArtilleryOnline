package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.service.UserBattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
@RequiredArgsConstructor
public class UserBattleEndpoint {

    private final UserBattleService userBattleService;

    @GetMapping
    public BattleResponse getBattle() {
        return userBattleService.getBattle();
    }

    @GetMapping("/tracking")
    public String getBattleTracking() {
        return userBattleService.getBattleTracking();
    }

    @PutMapping("/test-drive")
    public BattleResponse testDrive() {
        return userBattleService.createTestDrive();
    }

    @DeleteMapping("/leave")
    public void leaveBattle() {
        userBattleService.leaveBattle();
    }
}
