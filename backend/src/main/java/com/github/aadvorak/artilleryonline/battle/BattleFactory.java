package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.RoomConfig;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.preset.*;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleOnGroundProcessor;
import com.github.aadvorak.artilleryonline.battle.specs.*;
import com.github.aadvorak.artilleryonline.battle.state.*;
import com.github.aadvorak.artilleryonline.battle.statistics.UserBattleStatistics;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.dto.UserVehicleConfigDto;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.entity.UserSetting;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.repository.UserSettingRepository;
import com.github.aadvorak.artilleryonline.service.UserVehicleConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class BattleFactory {

    private final UserSettingRepository userSettingRepository;

    private final UserVehicleConfigService userVehicleConfigService;

    private final ApplicationSettings applicationSettings;

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
                .setUserVehicleNameMap(createUserVehicleNameMap(participants))
                .setActiveUserIds(new HashSet<>(userMap.keySet()));
        battle.getQueues().setUserCommandQueues(createUserCommandQueues(participants));
        return battle;
    }

    private RoomModel createRoomModel() {
        var roomModel = new RoomModel();
        var specs = RoomSpecsPreset.DEFAULT.getSpecs();
        var state = new RoomState()
                .setGroundLine(createGroundLine(specs))
                .setSurfaces(applicationSettings.isCreateSurfaces() ? createSurfaces(specs) : null);
        var config = new RoomConfig()
                .setBackground(BattleUtils.generateRandom(1, 7))
                .setGroundTexture(BattleUtils.generateRandom(1, 6));
        roomModel.setSpecs(specs);
        roomModel.setState(state);
        roomModel.setConfig(config);
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
        var amplitude = BattleUtils.generateRandom(0.01, 0.02);
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

    private List<SurfaceState> createSurfaces(RoomSpecs roomSpecs) {
        var halfLength = 2;
        var y = BattleUtils.getRoomHeight(roomSpecs) * 0.7;
        var x = BattleUtils.getRoomWidth(roomSpecs) / 2;
        var surfaces = new ArrayList<SurfaceState>();
        surfaces.add(new SurfaceState()
                .setBegin(new Position().setX(x - halfLength).setY(y))
                .setEnd(new Position().setX(x + halfLength).setY(y))
        );
        surfaces.add(new SurfaceState()
                .setBegin(new Position().setX(x - 4 * halfLength).setY(y - halfLength))
                .setEnd(new Position().setX(x - 2 * halfLength).setY(y + halfLength))
        );
        surfaces.add(new SurfaceState()
                .setBegin(new Position().setX(x + 4 * halfLength).setY(y - halfLength))
                .setEnd(new Position().setX(x + 2 * halfLength).setY(y + halfLength))
        );
        return surfaces;
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
            var userConfig = participant.getUser() != null
                    ? userVehicleConfigService.load(participant.getParams().getSelectedVehicle(), participant.getUser().getId())
                    : new UserVehicleConfigDto();
            vehicleModel.setPreCalc(new VehiclePreCalc(vehicleModel.getSpecs()));
            var gun = vehicleModel.getSpecs().getAvailableGuns().get(userConfig.getGun() != null
                    ? userConfig.getGun() : vehicleModel.getSpecs().getDefaultGun());
            JetSpecs jet = null;
            if (!vehicleModel.getSpecs().getAvailableJets().isEmpty()) {
                jet = vehicleModel.getSpecs().getAvailableJets().values().iterator().next();
            }
            DroneSpecs drone = null;
            if (!vehicleModel.getSpecs().getAvailableDrones().isEmpty()) {
                drone = vehicleModel.getSpecs().getAvailableDrones().values().iterator().next();
            }
            BomberSpecs bomber = null;
            BomberState bomberState = null;
            if (!vehicleModel.getSpecs().getAvailableBombers().isEmpty()) {
                bomber = vehicleModel.getSpecs().getAvailableBombers().values().iterator().next();
                bomberState = new BomberState()
                        .setRemainFlights(bomber.getFlights());
            }
            var availableShellsNumber = gun.getAvailableShells().size();
            var ammo = new HashMap<String, Integer>();
            gun.getAvailableShells().keySet().forEach(shellName ->
                    ammo.put(shellName, getAmmo(userConfig, shellName, gun.getAmmo(), availableShellsNumber)));
            var availableMissilesNumber = vehicleModel.getSpecs().getAvailableMissiles().size();
            var missiles = new HashMap<String, Integer>();
            vehicleModel.getSpecs().getAvailableMissiles().keySet().forEach(missileName ->
                    missiles.put(missileName, vehicleModel.getSpecs().getMissiles() / availableMissilesNumber));
            vehicleModel.setConfig(new VehicleConfig()
                    .setAmmo(ammo)
                    .setMissiles(missiles)
                    .setGun(gun)
                    .setJet(jet)
                    .setDrone(drone)
                    .setBomber(bomber)
                    .setColor(getVehicleColor(participant)));
            vehicleModel.setState(new VehicleState()
                    .setGunAngle(Math.PI / 2)
                    .setAmmo(new HashMap<>(vehicleModel.getConfig().getAmmo()))
                    .setMissiles(new HashMap<>(vehicleModel.getConfig().getMissiles()))
                    .setHitPoints(vehicleModel.getSpecs().getHitPoints())
                    .setPosition(new BodyPosition()
                            .setX(distanceBetweenVehicles * vehicleNumber)
                            .setY(BattleUtils.getRoomHeight(battleModel.getRoom().getSpecs()) / 2))
                    .setGunState(new GunState()
                            .setSelectedShell(ammo.keySet().stream().findAny().orElseThrow())
                            .setTriggerPushed(false))
                    .setTrackState(new TrackState())
                    .setJetState(jet == null ? null : new JetState()
                            .setVolume(jet.getCapacity())
                            .setActive(false))
                    .setDroneState(drone == null ? null : new DroneInVehicleState())
                    .setBomberState(bomberState));
            VehicleOnGroundProcessor.estimateVehicleAngleByPosition(vehicleModel, battleModel.getRoom());
            VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.getRoom());
            vehicles.put(participant.getNickname(), vehicleModel);
            vehicleNumber++;
        }
        return vehicles;
    }

    private int getAmmo(UserVehicleConfigDto userConfig, String shellName, int ammo, int availableShellsNumber) {
        if (userConfig.getAmmo() != null) {
            var userAmount = userConfig.getAmmo().get(shellName);
            if (userAmount != null) {
                return userAmount;
            }
        }
        // todo set default number of shells
        final var maxSgnShells = 10;
        if (shellName.equals(ShellSpecsPreset.LIGHT_SGN.getName())) {
            return maxSgnShells;
        }
        if (shellName.equals(ShellSpecsPreset.LIGHT_AP.getName())) {
            return ammo - maxSgnShells;
        }
        return ammo / availableShellsNumber;
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

    private Map<Long, String> createUserVehicleNameMap(Set<BattleParticipant> participants) {
        var userVehicleNameMap = new HashMap<Long, String>();
        participants.stream()
                .filter(participant -> participant.getUser() != null)
                .forEach(element -> userVehicleNameMap.put(element.getUser().getId(),
                        element.getParams().getSelectedVehicle()));
        return userVehicleNameMap;
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
