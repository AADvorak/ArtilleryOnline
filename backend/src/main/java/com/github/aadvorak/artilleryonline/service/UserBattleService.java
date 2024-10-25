package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.BattleFactory;
import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import com.github.aadvorak.artilleryonline.battle.BattleParticipantParams;
import com.github.aadvorak.artilleryonline.battle.BattleRunner;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueueParams;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBattleService {

    private final UserBattleMap userBattleMap;

    private final UserService userService;

    private final BattleRunner battleRunner;

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

    public BattleResponse createTestDrive(UserBattleQueueParams params) {
        var user = userService.getUserFromContext();
        var existingBattle = userBattleMap.get(user.getId());
        if (existingBattle != null) {
            return mapper.map(existingBattle, BattleResponse.class);
        }
        var userParticipant = new BattleParticipant()
                .setUser(user)
                .setNickname(user.getNickname())
                .setParams(new BattleParticipantParams()
                        .setSelectedVehicle(params.getSelectedVehicle()));
        var otherParticipant = new BattleParticipant()
                .setNickname("Dummy player")
                .setParams(new BattleParticipantParams()
                        .setSelectedVehicle(params.getSelectedVehicle()));
        var battle = new BattleFactory().createBattle(Set.of(userParticipant, otherParticipant));
        userBattleMap.put(user.getId(), battle);
        battleRunner.runBattle(battle);
        log.info("createTestDrive: user {}, battle {}, map size {}", user.getNickname(),
                battle.getId(), userBattleMap.size());
        return mapper.map(battle, BattleResponse.class);
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
