package com.github.aadvorak.artilleryonline.battle.processor.drone;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.DroneConfig;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.precalc.DronePreCalc;
import com.github.aadvorak.artilleryonline.battle.preset.DroneSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.BattleProcessor;
import com.github.aadvorak.artilleryonline.battle.state.DroneState;
import com.github.aadvorak.artilleryonline.battle.state.GunState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DroneLaunchProcessor implements BattleProcessor {

    private static final long LAUNCH_DELAY = 10000;

    private static final List<String> colors = List.of("#ff5733", "#ffd733", "#ff9633");

    public void process(Battle battle) {
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
        var specs = DroneSpecsPreset.values()[BattleUtils.generateRandom(0, DroneSpecsPreset.values().length)].getSpecs();
        var gunSpecs = specs.getAvailableGuns().values().iterator().next();
        var shellName = gunSpecs.getAvailableShells().keySet().iterator().next();
        var config = new DroneConfig()
                .setGun(gunSpecs)
                .setAmmo(Map.of(shellName, gunSpecs.getAmmo()))
                .setColor(colors.get(BattleUtils.generateRandom(0, colors.size())));
        var radius = specs.getEnginesRadius();
        var x = BattleUtils.generateRandom(radius, BattleUtils.getRoomWidth(roomSpecs) - radius);
        var y = 1.1 * BattleUtils.getRoomHeight(roomSpecs);
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
        model.setPreCalc(new DronePreCalc(specs));
        model.setConfig(config);
        model.setSpecs(specs);
        battle.getModel().getDrones().put(id, model);
        battle.getModel().getUpdates().addDrone(model);
        battle.setDroneLaunchTime(battle.getAbsoluteTime());
    }
}
