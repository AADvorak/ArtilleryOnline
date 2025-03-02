package com.github.aadvorak.artilleryonline.battle.processor.drone;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.DroneConfig;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.preset.DroneSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.state.DroneState;
import com.github.aadvorak.artilleryonline.battle.state.GunState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

import java.util.HashMap;
import java.util.Map;

public class DroneLaunchProcessor {

    private static final long LAUNCH_DELAY = 10000;

    public static void launch(Battle battle) {
        if (!BattleType.DRONE_HUNT.equals(battle.getType())) {
            return;
        }
        if (battle.getModel().getDrones().size() > 5) {
            return;
        }
        if (battle.getAbsoluteTime() < battle.getDroneLaunchTime() + LAUNCH_DELAY) {
            return;
        }
        var roomSpecs = battle.getModel().getRoom().getSpecs();
        var specs = DroneSpecsPreset.DEFAULT.getSpecs();
        var gunSpecs = specs.getAvailableGuns().values().iterator().next();
        var shellName = gunSpecs.getAvailableShells().keySet().iterator().next();
        var config = new DroneConfig()
                .setGun(gunSpecs)
                .setAmmo(Map.of(shellName, specs.getAmmo()));
        var x = BattleUtils.generateRandom(1.0, BattleUtils.getRoomWidth(roomSpecs) - 10.0);
        var y = BattleUtils.getRoomHeight(roomSpecs) + 1.0;
        var state = new DroneState()
                .setPosition(BodyPosition.of(new Position().setX(x).setY(y), 0.0))
                .setAmmo(new HashMap<>(config.getAmmo()))
                .setGunState(new GunState()
                        .setSelectedShell(shellName)
                        .setTriggerPushed(false))
                .setGunAngle(- Math.PI / 2);
        var id = battle.getModel().getIdGenerator().generate();
        var model = new DroneModel();
        model.setId(id);
        model.setState(state);
        model.setConfig(config);
        model.setSpecs(specs);
        battle.getModel().getDrones().put(id, model);
        battle.getModel().getUpdates().addDrone(model);
        battle.setDroneLaunchTime(battle.getAbsoluteTime());
    }
}
