package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.*;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.collection.BattleTrackingMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.collection.UserBattleQueueParams;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import com.github.aadvorak.artilleryonline.ws.BattleStartedSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleService {

    private static final List<String> BOT_NICKNAMES = List.of(
            "Leon",
            "Markoyo",
            "Pikor",
            "Fervor",
            "Jimmy",
            "Leyson",
            "Kiri",
            "CallMeBot"
    );

    private static final List<String> BOT_COLORS = List.of(
            "#ff5733",
            "#ffd733",
            "#ff9633",
            "#bf04ec",
            "#5dfc02",
            "#02f4fc",
            "#ffffff",
            "#fc0688"
    );

    private final UserBattleMap userBattleMap;

    private final BattleTrackingMap battleTrackingMap;

    private final UserService userService;

    private final BattleStarter battleStarter;

    private final UserAvailabilityService userAvailabilityService;

    private final MessageService messageService;

    private final BattleStartedSender battleStartedSender;

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
            var participants = room.getParticipants();
            var battle = battleStarter.start(participants, BattleType.ROOM);
            room.setBattle(battle);
            battle.setRoom(room);
            participants.stream()
                    .map(BattleParticipant::getUser)
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
                            .forEach(participant -> {
                                participant.setNickname(makeUniqueNickname(getRandomBotNickname(), participants));
                                participant.setParams(new BattleParticipantParams()
                                        .setSelectedVehicle(getRandomVehicle())
                                        .setVehicleColor(getRandomBotColor()));
                            });
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
                            .setSelectedVehicle(getRandomVehicle()));
            battleStarter.start(Set.of(userParticipant, otherParticipant), battleType);
        }
    }

    private String getRandomVehicle() {
        var vehicles = Arrays.stream(VehicleSpecsPreset.values()).map(VehicleSpecsPreset::getName).toList();
        return vehicles.get(BattleUtils.generateRandom(0, vehicles.size()));
    }

    private String getRandomBotNickname() {
        return BOT_NICKNAMES.get(BattleUtils.generateRandom(0, BOT_NICKNAMES.size()));
    }

    private String getRandomBotColor() {
        return BOT_COLORS.get(BattleUtils.generateRandom(0, BOT_COLORS.size()));
    }

    private String makeUniqueNickname(String nickname, Set<BattleParticipant> participants) {
        var existingNicknames = participants.stream()
                .map(BattleParticipant::getNickname)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        while (existingNicknames.contains(nickname)) {
            nickname += "1";
        }
        return nickname;
    }
}
