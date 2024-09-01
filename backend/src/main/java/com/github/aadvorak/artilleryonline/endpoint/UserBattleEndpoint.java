package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleStateResponse;
import com.github.aadvorak.artilleryonline.service.UserBattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/battles")
@RequiredArgsConstructor
public class UserBattleEndpoint {

    private final UserBattleService userBattleService;

    @GetMapping
    public BattleResponse getBattle(@CookieValue("UserKey") String userKey) {
        return userBattleService.getBattle(userKey);
    }

    @GetMapping("/state")
    public BattleStateResponse getBattleState(@CookieValue("UserKey") String userKey) {
        return userBattleService.getBattleState(userKey);
    }
}
