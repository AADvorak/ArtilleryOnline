package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.preset.*;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleOnGroundProcessor;
import com.github.aadvorak.artilleryonline.battle.specs.JetSpecs;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.state.*;
import com.github.aadvorak.artilleryonline.battle.statistics.UserBattleStatistics;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.entity.UserSetting;
import com.github.aadvorak.artilleryonline.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class BattleFactory {

    private final UserSettingRepository userSettingRepository;

    public Battle createBattle(Set<BattleParticipant> participants, BattleType battleType) {
        var battleModel = new BattleModel()
                .setRoom(createRoomModel());
        battleModel.setVehicles(createVehicles(participants, battleModel));
        battleModel.setStatistics(createUserBattleStatistics(participants));
        var userMap = createUserMap(participants);
        var battle = new Battle()
                .setTime(0)
                .setBattleStage(BattleStage.WAITING)
                .setType(battleType)
                .setModel(battleModel)
                .setUserMap(userMap)
                .setActiveUserIds(new HashSet<>(userMap.keySet()));
        battle.getQueues().setUserCommandQueues(createUserCommandQueues(participants));
        return battle;
    }

    private RoomModel createRoomModel() {
        var roomModel = new RoomModel();
        var specs = RoomSpecsPreset.DEFAULT.getSpecs();
        var state = new RoomState().setGroundLine(createGroundLine(specs));
        roomModel.setSpecs(specs);
        roomModel.setState(state);
        return roomModel;
    }

    private List<Double> createGroundLine(RoomSpecs roomSpecs) {
        var groundLine = new ArrayList<Double>();
        var xMin = roomSpecs.getLeftBottom().getX();
        var xMax = roomSpecs.getRightTop().getX();
        var height = roomSpecs.getRightTop().getY() - roomSpecs.getLeftBottom().getY();
        var sigma = BattleUtils.generateRandom(0.9, 1.5);
        var sigma1 = BattleUtils.generateRandom(1.0, 1.5);
        var sigma2 = BattleUtils.generateRandom(1.0, 1.5);
        var amplitude = BattleUtils.generateRandom(0.01, 0.025);
        var frequency = BattleUtils.generateRandom(5.0, 20.0);
        var mu = (xMax - xMin) / 2;
        var mu1 = mu - 0.3 * mu - BattleUtils.generateRandom(0.0, 0.6 * mu);
        var mu2 = mu + 0.3 * mu + BattleUtils.generateRandom(0.0, 0.6 * mu);
        for (var x = xMin; x <= xMax; x += roomSpecs.getStep()) {
            var y = 1.0 + height * BattleUtils.gaussian(x, sigma, mu);
            y += 0.6 * height * BattleUtils.gaussian(x, sigma1, mu1);
            y += 0.6 * height * BattleUtils.gaussian(x, sigma2, mu2);
            y += amplitude * Math.sin(frequency * x);
            groundLine.add(y > 0 ? y : 0.0);
        }
        return groundLine;
    }

    private Map<String, VehicleModel> createVehicles(Set<BattleParticipant> participants, BattleModel battleModel) {
        var vehicles = new HashMap<String, VehicleModel>();
        var distanceBetweenVehicles = (battleModel.getRoom().getSpecs().getRightTop().getX()
                - battleModel.getRoom().getSpecs().getLeftBottom().getX())
                / (participants.size() + 1);
        var vehicleNumber = 1;
        for (var participant : participants) {
            var vehicleModel = new VehicleModel();
            vehicleModel.setId(battleModel.getIdGenerator().generate());
            if (participant.getUser() != null) {
                vehicleModel.setUserId(participant.getUser().getId());
            }
            vehicleModel.setSpecs(Arrays.stream(VehicleSpecsPreset.values())
                    .filter(preset -> preset.getName().equals(participant.getParams().getSelectedVehicle()))
                    .map(VehicleSpecsPreset::getSpecs).findAny().orElseThrow());
            vehicleModel.setPreCalc(new VehiclePreCalc(vehicleModel.getSpecs()));
            var gun = vehicleModel.getSpecs().getAvailableGuns().values().iterator().next();
            JetSpecs jet = null;
            if (!vehicleModel.getSpecs().getAvailableJets().isEmpty()) {
                jet = vehicleModel.getSpecs().getAvailableJets().values().iterator().next();
            }
            var availableShellsNumber = gun.getAvailableShells().size();
            var ammo = new HashMap<String, Integer>();
            gun.getAvailableShells().keySet().forEach(shellName ->
                    ammo.put(shellName, vehicleModel.getSpecs().getAmmo() / availableShellsNumber));
            vehicleModel.setConfig(new VehicleConfig()
                    .setAmmo(ammo)
                    .setGun(gun)
                    .setJet(jet)
                    .setColor(getVehicleColor(participant)));
            vehicleModel.setState(new VehicleState()
                    .setAngle(0)
                    .setGunAngle(Math.PI / 2)
                    .setAmmo(new HashMap<>(vehicleModel.getConfig().getAmmo()))
                    .setHitPoints(vehicleModel.getSpecs().getHitPoints())
                    .setPosition(new Position()
                            .setX(distanceBetweenVehicles * vehicleNumber)
                            .setY(BattleUtils.getRoomHeight(battleModel.getRoom().getSpecs()) / 2))
                    .setGunState(new GunState()
                            .setSelectedShell(ammo.keySet().stream().findAny().orElseThrow())
                            .setTriggerPushed(false))
                    .setTrackState(new TrackState())
                    .setJetState(jet == null ? null : new JetState()
                            .setVolume(jet.getCapacity())
                            .setActive(false)));
            VehicleOnGroundProcessor.estimateVehicleAngleByPosition(vehicleModel, battleModel.getRoom());
            VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.getRoom());
            vehicles.put(participant.getNickname(), vehicleModel);
            vehicleNumber++;
        }
        return vehicles;
    }

    private Map<Long, Queue<UserCommand>> createUserCommandQueues(Set<BattleParticipant> participants) {
        var userCommandQueues = new HashMap<Long, Queue<UserCommand>>();
        participants.stream()
                .filter(participant -> participant.getUser() != null)
                .forEach(element -> userCommandQueues.put(element.getUser().getId(), new ConcurrentLinkedQueue<>()));
        return userCommandQueues;
    }

    private Map<Long, User> createUserMap(Set<BattleParticipant> participants) {
        var userNicknameMap = new HashMap<Long, User>();
        participants.stream()
                .filter(participant -> participant.getUser() != null)
                .forEach(element -> userNicknameMap.put(element.getUser().getId(), element.getUser()));
        return userNicknameMap;
    }

    private Map<Long, UserBattleStatistics> createUserBattleStatistics(Set<BattleParticipant> participants) {
        var userBattleStatistics = new HashMap<Long, UserBattleStatistics>();
        participants.stream()
                .filter(participant -> participant.getUser() != null)
                .forEach(element -> userBattleStatistics.put(element.getUser().getId(),
                        new UserBattleStatistics()));
        return userBattleStatistics;
    }

    private String getVehicleColor(BattleParticipant participant) {
        if (participant.getUser() == null) {
            return null;
        }
        var colorSetting = userSettingRepository.findByUserIdAndGroupNameAndName(participant.getUser().getId(),
                "appearances", "vehicleColor");
        return colorSetting.map(UserSetting::getValue).orElse(null);
    }
}
