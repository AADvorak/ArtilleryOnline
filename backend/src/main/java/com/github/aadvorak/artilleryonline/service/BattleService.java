package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.*;
import com.github.aadvorak.artilleryonline.collection.BattleTrackingMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueueParams;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import com.github.aadvorak.artilleryonline.ws.BattleStartedSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleService {

    private final UserBattleMap userBattleMap;

    private final BattleTrackingMap battleTrackingMap;

    private final UserService userService;

    private final BattleStarter battleStarter;

    private final UserAvailabilityService userAvailabilityService;

    private final MessageService messageService;

    private final BattleStartedSender battleStartedSender;

    private final ModelMapper mapper = new ModelMapper();

    private final BotsService botsService = new BotsService();

    public BattleResponse getBattle() {
        var user = userService.getUserFromContext();
        var battle = userBattleMap.get(user.getId());
        if (battle == null) {
            throw new NotFoundAppException();
        }
        log.info("getBattle: user {}, battle {}, map size {}", user.getNickname(), battle.getId(), userBattleMap.size());
        var response = mapper.map(battle, BattleResponse.class);
        response.setUserNicknames(battle.getUserMap().values().stream()
                .map(User::getNickname).collect(Collectors.toSet()));
        return response;
    }

    public String getBattleTracking(String battleId) {
        var tracking = battleTrackingMap.get(battleId);
        if (tracking == null) {
            throw new NotFoundAppException();
        }
        battleTrackingMap.remove(battleId);
        return tracking;
    }

    public void createTestDrive(UserBattleQueueParams params) {
        createTestBattle(params, BattleType.TEST_DRIVE);
    }

    public void createCollider(UserBattleQueueParams params) {
        createTestBattle(params, BattleType.COLLIDER);
    }

    public void createDroneHunt(UserBattleQueueParams params) {
        var user = userService.getUserFromContext();
        synchronized (userBattleMap) {
            userAvailabilityService.checkSingleBattleAvailability(user);
            var userParticipant = new BattleParticipant()
                    .setUser(user)
                    .setNickname(user.getNickname())
                    .setParams(new BattleParticipantParams()
                            .setSelectedVehicle(params.getSelectedVehicle()));
            battleStarter.start(Set.of(userParticipant), BattleType.DRONE_HUNT);
        }
    }

    public void createRoomBattle(Room room) {
        synchronized (userBattleMap) {
            checkReadyToBattle(room.getOwner());
            room.getGuests().values().forEach(this::checkReadyToBattle);
            var members = room.getMembers();
            var battle = battleStarter.start(members, room.getBattleType());
            room.setBattle(battle);
            battle.setRoom(room);
            members.stream()
                    .map(BattleParticipant::getUser)
                    .filter(Objects::nonNull)
                    .forEach(battleStartedSender::send);
        }
    }

    public void createRandomBattle(Set<BattleParticipant> participants) {
        synchronized (userBattleMap) {
            participants.stream()
                    .map(BattleParticipant::getUser)
                    .filter(Objects::nonNull)
                    .forEach(userAvailabilityService::checkSingleBattleAvailability);
            participants.stream()
                    .filter(participant -> participant.getUser() == null)
                            .forEach(participant -> botsService.fillBot(participant, participants));
            battleStarter.start(participants, BattleType.RANDOM);
            participants.stream()
                    .map(BattleParticipant::getUser)
                    .filter(Objects::nonNull)
                    .forEach(battleStartedSender::send);
        }
    }

    public void leaveBattle() {
        var user = userService.getUserFromContext();
        synchronized (userBattleMap) {
            var battle = userBattleMap.get(user.getId());
            if (battle != null) {
                battle.getActiveUserIds().remove(user.getId());
                battle.getQueues().getUserCommandQueues().remove(user.getId());
                userBattleMap.remove(user.getId());
                battle.getActiveUserIds().forEach(userId -> messageService.createMessage(
                        battle.getUserMap().get(userId),
                        "User " + user.getNickname() + " left the battle",
                        new Locale()
                                .setCode(LocaleCode.USER_LEFT_BATTLE)
                                .setParams(Map.of("nickname", user.getNickname()))));
                log.info("leaveBattle: user {}, battle {}, map size {}", user.getNickname(),
                        battle.getId(), userBattleMap.size());
            }
        }
    }

    private void checkReadyToBattle(BattleParticipant participant) {
        if (participant.getParams() == null || participant.getParams().getSelectedVehicle() == null) {
            throw new ConflictAppException("Not all players have selected vehicles",
                    new Locale().setCode(LocaleCode.NO_SELECTED_VEHICLES));
        }
        userAvailabilityService.checkRoomBattleAvailability(participant.getUser());
    }

    private void createTestBattle(UserBattleQueueParams params, BattleType battleType) {
        var user = userService.getUserFromContext();
        synchronized (userBattleMap) {
            userAvailabilityService.checkSingleBattleAvailability(user);
            var userParticipant = new BattleParticipant()
                    .setUser(user)
                    .setNickname(user.getNickname())
                    .setParams(new BattleParticipantParams()
                            .setSelectedVehicle(params.getSelectedVehicle()));
            var otherParticipant = new BattleParticipant()
                    .setNickname("Dummy player")
                    .setParams(new BattleParticipantParams()
                            .setSelectedVehicle(botsService.getRandomVehicle())
                            .setVehicleColor(botsService.getRandomColor()));
            battleStarter.start(Set.of(userParticipant, otherParticipant), battleType);
        }
    }
}
