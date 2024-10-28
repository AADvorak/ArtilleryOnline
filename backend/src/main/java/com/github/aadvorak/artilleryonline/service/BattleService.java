package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.*;
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
public class BattleService {

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

    public void createRoomBattle(Room room) {
        checkReadyToBattle(room.getOwner());
        room.getGuests().values().forEach(this::checkReadyToBattle);
        var participants = room.getParticipants();
        var battle = new BattleFactory().createBattle(participants);
        room.setBattle(battle);
        battle.setRoom(room);
        participants.forEach(participant -> userBattleMap.put(participant.getUser().getId(), battle));
        battleRunner.runBattle(battle);
        var nicknames = participants.stream().map(BattleParticipant::getNickname).toList();
        log.info("startBattle: users {}, battle {}, map size {}", nicknames,
                battle.getId(), userBattleMap.size());
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

    private void checkReadyToBattle(BattleParticipant participant) {
        if (participant.getParams() == null || participant.getParams().getSelectedVehicle() == null) {
            // todo
            throw new NotFoundAppException();
        }
        if (userBattleMap.get(participant.getUser().getId()) != null) {
            throw new NotFoundAppException();
        }
    }
}
