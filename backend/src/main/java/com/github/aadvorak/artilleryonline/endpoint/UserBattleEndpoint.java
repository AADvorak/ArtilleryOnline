package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.collection.UserBattleQueueParams;
import com.github.aadvorak.artilleryonline.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
@RequiredArgsConstructor
public class UserBattleEndpoint {

    private final BattleService battleService;

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getBattle() {
        return battleService.getBattle().serialize();
    }

    @GetMapping("/tracking")
    public String getBattleTracking() {
        return battleService.getBattleTracking();
    }

    @PostMapping(path = "/test-drive", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void testDrive(@RequestBody UserBattleQueueParams params) {
        battleService.createTestDrive(params);
    }

    @PostMapping(path = "/drone-hunt", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void droneHunt(@RequestBody UserBattleQueueParams params) {
        battleService.createDroneHunt(params);
    }

    @DeleteMapping("/leave")
    public void leaveBattle() {
        battleService.leaveBattle();
    }
}
