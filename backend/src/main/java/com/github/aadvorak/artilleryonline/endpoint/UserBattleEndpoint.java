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
    public  byte[] testDrive(@RequestBody UserBattleQueueParams params) {
        return battleService.createTestDrive(params).serialize();
    }

    @PostMapping(path = "/drone-hunt", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public  byte[] droneHunt(@RequestBody UserBattleQueueParams params) {
        return battleService.createDroneHunt(params).serialize();
    }

    @DeleteMapping("/leave")
    public void leaveBattle() {
        battleService.leaveBattle();
    }
}
