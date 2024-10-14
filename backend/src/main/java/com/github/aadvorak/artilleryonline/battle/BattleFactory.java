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
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.state.*;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class BattleFactory {

    public Battle createBattle(Set<String> userKeys) {
        var battleModel = new BattleModel()
                .setRoom(createRoomModel());
        battleModel.setVehicles(createVehicles(userKeys, battleModel));
        return new Battle()
                .setTime(0)
                .setBattleStage(BattleStage.WAITING)
                .setModel(battleModel)
                .setUserCommandQueues(createUserCommandQueues(userKeys));
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
        var sigma = BattleUtils.generateRandom(0.5, 1.5);
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

    private Map<String, VehicleModel> createVehicles(Set<String> userKeys, BattleModel battleModel) {
        var vehicles = new HashMap<String, VehicleModel>();
        var distanceBetweenVehicles = (battleModel.getRoom().getSpecs().getRightTop().getX()
                - battleModel.getRoom().getSpecs().getLeftBottom().getX())
                / (userKeys.size() + 1);
        var vehicleNumber = 1;
        for (String userKey : userKeys) {
            var vehicleModel = new VehicleModel();
            vehicleModel.setId(battleModel.getIdGenerator().generate());
            vehicleModel.setSpecs(VehicleSpecsPreset.DEFAULT.getSpecs());
            vehicleModel.setPreCalc(new VehiclePreCalc(vehicleModel.getSpecs()));
            var ammo = Map.of(
                    ShellSpecsPreset.DEFAULT_AP.getName(), vehicleModel.getSpecs().getAmmo() / 2,
                    ShellSpecsPreset.DEFAULT_HE.getName(), vehicleModel.getSpecs().getAmmo() / 2
            );
            vehicleModel.setConfig(new VehicleConfig()
                    .setAmmo(ammo)
                    .setGun(GunSpecsPreset.DEFAULT.getSpecs())
                    .setJet(JetSpecsPreset.DEFAULT.getSpecs()));
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
                    .setJetState(new JetState()
                            .setVolume(JetSpecsPreset.DEFAULT.getSpecs().getCapacity())
                            .setActive(false)));
            VehicleOnGroundProcessor.estimateVehicleAngleByPosition(vehicleModel, battleModel.getRoom());
            VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.getRoom());
            vehicles.put(userKey, vehicleModel);
            vehicleNumber++;
        }
        return vehicles;
    }

    private Map<String, Queue<UserCommand>> createUserCommandQueues(Set<String> userKeys) {
        var userCommandQueues = new HashMap<String, Queue<UserCommand>>();
        userKeys.forEach(userKey -> userCommandQueues.put(userKey, new ConcurrentLinkedQueue<>()));
        return userCommandQueues;
    }
}
