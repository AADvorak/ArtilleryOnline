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
import com.github.aadvorak.artilleryonline.battle.state.GunState;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class BattleFactory {

    public Battle createBattle(Set<String> userKeys) {
        var roomModel = new RoomModel();
        roomModel.setSpecs(RoomSpecsPreset.DEFAULT.getSpecs());
        var battleModel = new BattleModel()
                .setRoom(roomModel)
                .setShells(new HashMap<>());
        battleModel.setVehicles(createVehicles(userKeys, battleModel));
        return new Battle()
                .setTime(0)
                .setBattleStage(BattleStage.WAITING)
                .setModel(battleModel)
                .setUserCommandQueues(createUserCommandQueues(userKeys));
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
