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
                .setRoom(roomModel);
        var battle = new Battle()
                .setTime(0)
                .setBattleStage(BattleStage.WAITING);
        var userCommandQueues = new HashMap<String, Queue<UserCommand>>();
        var vehicles = new HashMap<String, VehicleModel>();
        var distanceBetweenVehicles = (roomModel.getSpecs().getRightTop().getX()
                - roomModel.getSpecs().getLeftBottom().getX())
                / (userKeys.size() + 1);
        var vehicleNumber = 1;
        for (String userKey : userKeys) {
            var vehicleModel = new VehicleModel();
            vehicleModel.setId(battleModel.getIdGenerator().generate());
            vehicleModel.setSpecs(VehicleSpecsPreset.DEFAULT.getSpecs());
            vehicleModel.setConfig(new VehicleConfig()
                    .setAmmo(Collections.singletonMap(ShellSpecsPreset.DEFAULT.getSpecs(), 30))
                    .setGun(GunSpecsPreset.DEFAULT.getSpecs()));
            vehicleModel.setState(new VehicleState()
                    .setAngle(0)
                    .setGunAngle(Math.PI / 2)
                    .setAmmo(vehicleModel.getConfig().getAmmo())
                    .setHeatPoints(vehicleModel.getSpecs().getHeatPoints())
                    .setPosition(new Position().setX(distanceBetweenVehicles * vehicleNumber).setY(0.0))
                    .setGunState(new GunState()
                            .setSelectedShell(ShellSpecsPreset.DEFAULT.getSpecs())
                            .setTriggerPushed(false)));
            vehicles.put(userKey, vehicleModel);
            userCommandQueues.put(userKey, new ConcurrentLinkedQueue<>());
            vehicleNumber++;
        }
        battleModel.setVehicles(vehicles);
        battle.setUserCommandQueues(userCommandQueues);
        return new Battle();
    }
}
