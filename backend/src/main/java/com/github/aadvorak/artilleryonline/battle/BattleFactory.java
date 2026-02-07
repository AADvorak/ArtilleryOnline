package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.AmmoConfig;
import com.github.aadvorak.artilleryonline.battle.config.BoxConfig;
import com.github.aadvorak.artilleryonline.battle.config.RoomConfig;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BoxPreCalc;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.preset.BoxSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.preset.RoomSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.preset.ShellSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleOnGroundProcessor;
import com.github.aadvorak.artilleryonline.battle.specs.*;
import com.github.aadvorak.artilleryonline.battle.state.*;
import com.github.aadvorak.artilleryonline.battle.statistics.PlayerBattleStatistics;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.dto.UserVehicleConfigDto;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.entity.UserSetting;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.repository.UserSettingRepository;
import com.github.aadvorak.artilleryonline.service.UserVehicleConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleFactory {

    private final UserSettingRepository userSettingRepository;

    private final UserVehicleConfigService userVehicleConfigService;

    private final ApplicationSettings applicationSettings;

    public Battle createBattle(Set<BattleParticipant> participants, BattleType battleType) {
        var battleModel = new BattleModel()
                .setRoom(createRoomModel(battleType));
        var battle = new Battle()
                .setTime(0)
                .setDuration(battleType.getDuration())
                .setBattleStage(BattleStage.WAITING)
                .setType(battleType)
                .setModel(battleModel)
                .setNicknameTeamMap(participants.stream()
                        .collect(Collectors.toMap(BattleParticipant::getNickname, BattleParticipant::getTeamId)));
        battleModel.setVehicles(createVehicles(participants, battle, battleType));
        if (BattleType.COLLIDER.equals(battleType)) {
            battleModel.setBoxes(createBoxes(battleModel));
        }
        battleModel.setStatistics(createUserBattleStatistics(participants));
        var userMap = createUserMap(participants);
        battle
                .setUserMap(userMap)
                .setUserVehicleNameMap(createUserVehicleNameMap(participants))
                .setActiveUserIds(new HashSet<>(userMap.keySet()));
        battle.getQueues().setUserCommandQueues(createUserCommandQueues(participants));
        return battle;
    }

    private RoomModel createRoomModel(BattleType battleType) {
        var roomModel = new RoomModel();
        var specs = BattleType.COLLIDER.equals(battleType)
                ? RoomSpecsPreset.NO_GRAVITY.getSpecs()
                : RoomSpecsPreset.DEFAULT.getSpecs();
        var state = new RoomState()
                .setGroundLine(applicationSettings.isCreateSurfaces() ? null : createGroundLine(specs))
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
        var height = 8.0;
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
        var surfaces = new ArrayList<SurfaceState>();
        var levelsNumber = 6;
        var roomHeight = BattleUtils.getRoomHeight(roomSpecs);
        var roomWidth = BattleUtils.getRoomWidth(roomSpecs);
        var xMin = roomSpecs.getLeftBottom().getX();
        var xMax = roomSpecs.getRightTop().getX();
        var yMax = roomSpecs.getRightTop().getY();
        var levelHeight = roomHeight / levelsNumber;
        for (var i = 1; i < levelsNumber; i++) {
            var y = yMax - levelHeight * i;
            var xLeft = xMin + BattleUtils.generateRandom(0.7, 1.0) * roomWidth / 3;
            surfaces.add(new SurfaceState()
                    .setBegin(new Position().setX(xMin).setY(y))
                    .setEnd(new Position().setX(xLeft).setY(y)));
            var xRight = xMax - BattleUtils.generateRandom(0.7, 1.0) * roomWidth / 3;
            surfaces.add(new SurfaceState()
                    .setBegin(new Position().setX(xMax).setY(y))
                    .setEnd(new Position().setX(xRight).setY(y)));
            if (i < levelsNumber - 1) {
                surfaces.add(new SurfaceState()
                        .setBegin(new Position().setX(i % 2 == 0 ? xRight : xLeft).setY(y))
                        .setEnd(new Position().setX(xMin + roomWidth / 2).setY(y - levelHeight)));
            }
        }
        return surfaces;
    }

    private Map<String, VehicleModel> createVehicles(Set<BattleParticipant> participants, Battle battle,
                                                     BattleType battleType) {
        var battleModel = battle.getModel();
        var vehicles = new HashMap<String, VehicleModel>();
        var roomWidth = BattleUtils.getRoomWidth(battleModel.getRoom().getSpecs());
        var roomLeftX = battleModel.getRoom().getSpecs().getLeftBottom().getX();
        var distancesBetweenVehicles = new HashMap<Integer, Double>();
        if (battle.getType().isTeam()) {
            distancesBetweenVehicles.put(0, 0.5 * roomWidth / (battle.getTeamNicknames(0).size() + 1));
            distancesBetweenVehicles.put(1, 0.5 * roomWidth / (battle.getTeamNicknames(1).size() + 1));
        } else {
            distancesBetweenVehicles.put(0, roomWidth / (participants.size() + 1));
        }
        var vehicleNumber = 1;
        var vehicleNumber1 = 1;
        for (var participant : participants) {
            var vehicleModel = new VehicleModel();
            var id = battleModel.getIdGenerator().generate();
            if (!BattleType.TEST_DRIVE.equals(battle.getType()) && participant.getUser() == null) {
                battle.getBotsData().getVehicleIds().add(id);
            }
            vehicleModel.setId(id);
            vehicleModel.setNickname(participant.getNickname());
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
            BomberSpecs bomberSpecs = null;
            BomberState bomberState = null;
            if (!vehicleModel.getSpecs().getAvailableBombers().isEmpty()) {
                bomberSpecs = vehicleModel.getSpecs().getAvailableBombers().values().iterator().next();
                bomberState = new BomberState()
                        .setRemainFlights(bomberSpecs.getFlights());
            }
            MissileLauncherSpecs missileLauncherSpecs = null;
            MissileLauncherState missileLauncherState = null;
            if (!vehicleModel.getSpecs().getAvailableMissileLaunchers().isEmpty()) {
                missileLauncherSpecs = vehicleModel.getSpecs().getAvailableMissileLaunchers().values().iterator().next();
                missileLauncherState = new MissileLauncherState()
                        .setRemainMissiles(vehicleModel.getSpecs().getMissiles());
            }
            var ammoConfig = getAmmoConfig(userConfig, gun.getAvailableShells().keySet(), gun.getAmmo());
            var teamId = battle.getType().isTeam() ? battle.getNicknameTeamMap().get(participant.getNickname()) : 0;
            var currentVehicleNumber = teamId == 0 ? vehicleNumber : vehicleNumber1;
            var vehicleX = roomLeftX + teamId * roomWidth / 2
                    + distancesBetweenVehicles.get(teamId) * currentVehicleNumber;
            vehicleModel.setConfig(new VehicleConfig()
                    .setAmmo(ammoConfig)
                    .setMissileLauncher(missileLauncherSpecs)
                    .setGun(gun)
                    .setJet(jet)
                    .setDrone(drone)
                    .setBomber(bomberSpecs)
                    .setColor(getVehicleColor(participant)));
            vehicleModel.setState(new VehicleState()
                    .setAmmo(ammoConfig.stream().collect(Collectors.toMap(AmmoConfig::getName, AmmoConfig::getAmount)))
                    .setMissileLauncherState(missileLauncherState)
                    .setHitPoints(vehicleModel.getSpecs().getHitPoints())
                    .setPosition(new BodyPosition()
                            .setX(vehicleX)
                            .setY(BattleUtils.getRoomHeight(battleModel.getRoom().getSpecs()) / 2))
                    .setGunState(new GunState()
                            .setAngle(Math.PI / 2)
                            .setTargetAngle(Math.PI / 2)
                            .setSelectedShell(ammoConfig.stream().findFirst().map(AmmoConfig::getName).orElseThrow()))
                    .setTrackState(new TrackState())
                    .setJetState(jet == null ? null : new JetState()
                            .setVolume(jet.getCapacity())
                            .setActive(false))
                    .setDroneState(drone == null ? null : new DroneInVehicleState())
                    .setBomberState(bomberState));
            if (!BattleType.COLLIDER.equals(battleType)) {
                VehicleOnGroundProcessor.estimateVehicleAngleByPosition(vehicleModel, battleModel.getRoom());
                VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.getRoom());
            }
            vehicles.put(participant.getNickname(), vehicleModel);
            if (teamId == 0) {
                vehicleNumber++;
            } else {
                vehicleNumber1++;
            }
        }
        return vehicles;
    }

    private List<AmmoConfig> getAmmoConfig(UserVehicleConfigDto userConfig, Set<String> availableShellsNames, int ammo) {
        if (userConfig.getAmmo() != null) {
            return userConfig.getAmmo();
        }
        var availableShellsNumber = availableShellsNames.size();
        return availableShellsNames.stream()
                .sorted()
                .map(shellName -> new AmmoConfig()
                        .setName(shellName)
                        .setAmount(getDefaultShellsNumber(shellName, ammo, availableShellsNumber)))
                .toList();
    }

    private int getDefaultShellsNumber(String shellName, int ammo, int availableShellsNumber) {
        final var maxSgnShells = 10;
        final var nonSignalLightShells = List.of(ShellSpecsPreset.LIGHT_AP.getName(), ShellSpecsPreset.LIGHT_HE.getName());
        if (shellName.equals(ShellSpecsPreset.LIGHT_SGN.getName())) {
            return maxSgnShells;
        }
        if (nonSignalLightShells.contains(shellName)) {
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

    private Map<String, PlayerBattleStatistics> createUserBattleStatistics(Set<BattleParticipant> participants) {
        var userBattleStatistics = new HashMap<String, PlayerBattleStatistics>();
        participants.forEach(element -> userBattleStatistics.put(element.getNickname(),
                        new PlayerBattleStatistics()));
        return userBattleStatistics;
    }

    private String getVehicleColor(BattleParticipant participant) {
        if (participant.getParams().getVehicleColor() != null) {
            return participant.getParams().getVehicleColor();
        }
        if (participant.getUser() == null) {
            return null;
        }
        var colorSetting = userSettingRepository.findByUserIdAndGroupNameAndName(participant.getUser().getId(),
                "appearances", "vehicleColor");
        return colorSetting.map(UserSetting::getValue).orElse(null);
    }

    private Map<Integer, BoxModel> createBoxes(BattleModel battleModel) {
        var rightTop = battleModel.getRoom().getSpecs().getRightTop();
        var leftBottom = battleModel.getRoom().getSpecs().getLeftBottom();
        var y = (rightTop.getY() + leftBottom.getY()) / 2;
        var map = new HashMap<Integer, BoxModel>();
        putBox(map, battleModel, new BodyPosition().setX(rightTop.getX() - 1.0).setY(y));
        putBox(map, battleModel, new BodyPosition().setX(leftBottom.getX() + 1.0).setY(y));
        return map;
    }

    private void putBox(Map<Integer, BoxModel> map, BattleModel battleModel, BodyPosition position) {
        var specs = BoxSpecsPreset.values()[BattleUtils.generateRandom(0, BoxSpecsPreset.values().length)].getSpecs();
        var preCalc = new BoxPreCalc(specs);
        var config = new BoxConfig()
                .setColor("#FF0000")
                .setAmount(BattleUtils.generateRandom(10.0, 30.0));
        var state = new BoxState()
                .setPosition(position);
        var id = battleModel.getIdGenerator().generate();
        var model = new BoxModel();
        model.setId(id);
        model.setState(state);
        model.setPreCalc(preCalc);
        model.setConfig(config);
        model.setSpecs(specs);
        map.put(id, model);
    }
}
