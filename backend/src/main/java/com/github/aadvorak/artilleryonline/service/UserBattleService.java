package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBattleService {

    private final UserBattleMap userBattleMap;

    private final UserService userService;

    private final ModelMapper mapper = new ModelMapper();

    public BattleResponse getBattle() {
        var user = userService.getUserFromContext();
        var battle = userBattleMap.get(user.getId());
        if (battle == null) {
            throw new NotFoundAppException();
        }
        log.info("getBattle: user {}, battle {}, map size {}", user.getNickname(), battle.getId(), userBattleMap.size());
        return mapper.map(battle, BattleResponse.class);
    }

    public String getBattleTracking() {
        var user = userService.getUserFromContext();
        var battle = userBattleMap.get(user.getId());
        if (battle == null || battle.getTracking() == null) {
            throw new NotFoundAppException();
        }
        return battle.getTracking();
    }

    public BattleResponse createTestDrive() {
        var user = userService.getUserFromContext();
        var existingBattle = userBattleMap.get(user.getId());
        if (existingBattle != null) {
            return mapper.map(existingBattle, BattleResponse.class);
        }
        // todo logic
        return new BattleResponse();
    }

    public void leaveBattle() {
        var user = userService.getUserFromContext();
        var battle = userBattleMap.get(user.getId());
        if (battle != null) {
            battle.getUserNicknameMap().remove(user.getId());
            battle.getUserCommandQueues().remove(user.getId());
            userBattleMap.remove(user.getId());
            log.info("leaveBattle: user {}, battle {}, map size {}", user.getNickname(),
                    battle.getId(), userBattleMap.size());
        }
    }
}
