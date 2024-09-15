package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleStateResponse;
import com.github.aadvorak.artilleryonline.service.UserBattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
@RequiredArgsConstructor
public class UserBattleEndpoint {

    private final UserBattleService userBattleService;

    @GetMapping
    public BattleResponse getBattle(@RequestHeader("UserKey") String userKey) {
        return userBattleService.getBattle(userKey);
    }

    @GetMapping("/state")
    public BattleStateResponse getBattleState(@RequestHeader("UserKey") String userKey) {
        return userBattleService.getBattleState(userKey);
    }
}
