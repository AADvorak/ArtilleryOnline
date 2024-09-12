package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.GunSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.preset.RoomSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.preset.ShellSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.state.GunState;
import com.github.aadvorak.artilleryonline.battle.state.RoomState;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class BattleFactory {

    public Battle createBattle(Set<String> userKeys) {
        var battleModel = new BattleModel()
                .setRoom(createRoomModel())
                .setShells(new HashMap<>());
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
        var mu = (xMax - xMin) / 2;
        for (var x = xMin; x <= xMax; x += roomSpecs.getStep()) {
            var y = (height / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp(-0.5 * Math.pow((x - mu) / sigma, 2.0));
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
            var ammo = Collections.singletonMap(ShellSpecsPreset.DEFAULT.getName(), 30);
            vehicleModel.setId(battleModel.getIdGenerator().generate());
            vehicleModel.setSpecs(VehicleSpecsPreset.DEFAULT.getSpecs());
            vehicleModel.setConfig(new VehicleConfig()
                    .setAmmo(ammo)
                    .setGun(GunSpecsPreset.DEFAULT.getSpecs()));
            vehicleModel.setState(new VehicleState()
                    .setAngle(0)
                    .setGunAngle(Math.PI / 2)
                    .setAmmo(new HashMap<>(vehicleModel.getConfig().getAmmo()))
                    .setHitPoints(vehicleModel.getSpecs().getHitPoints())
                    .setPosition(new Position().setX(distanceBetweenVehicles * vehicleNumber).setY(0.0))
                    .setGunState(new GunState()
                            .setSelectedShell(ammo.keySet().stream().findAny().orElseThrow())
                            .setTriggerPushed(false)));
            BattleUtils.correctVehiclePositionAndAngleOnGround(vehicleModel.getState(), battleModel.getRoom());
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
