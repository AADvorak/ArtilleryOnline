package com.github.aadvorak.artilleryonline.battle.processor.box;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.BoxConfig;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BoxPreCalc;
import com.github.aadvorak.artilleryonline.battle.preset.BoxSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.BattleProcessor;
import com.github.aadvorak.artilleryonline.battle.specs.BoxSpecs;
import com.github.aadvorak.artilleryonline.battle.state.BoxState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class BoxDropProcessor implements BattleProcessor {

    private static final long DROP_DELAY = 30000;
    private static final int MAX_BOXES = 2;
    private static final List<String> COLORS = List.of(
            "#FF0000",  // Red
            "#FF7F00",  // Orange
            "#00FF00",  // Green
            "#0000FF",  // Blue
            "#8B00FF"   // Violet
    );

    public void process(Battle battle) {
        var maxBoxes = Math.min(MAX_BOXES, battle.getModel().getVehicles().size());
        if (BattleType.COLLIDER.equals(battle.getType())
                || battle.getModel().getBoxes().size() >= maxBoxes
                || battle.getAbsoluteTime() < battle.getBoxDropTime() + DROP_DELAY) {
            return;
        }
        var vehicles = battle.getModel().getVehicles().values();
        var hasEmptyAmmo = hasEmptyAmmo(vehicles);
        var hasLowHp = hasLowHp(vehicles);
        if (hasEmptyAmmo || hasLowHp) {
            var roomSpecs = battle.getModel().getRoom().getSpecs();
            var specs = getSpecs(hasEmptyAmmo, hasLowHp);
            var preCalc = new BoxPreCalc(specs);
            var config = new BoxConfig()
                    .setColor(COLORS.get(BattleUtils.generateRandom(0, COLORS.size())))
                    .setAmount(BattleUtils.generateRandom(10.0, 30.0));
            var radius = preCalc.getMaxRadius();
            var x = BattleUtils.generateRandom(radius, BattleUtils.getRoomWidth(roomSpecs) - radius);
            var y = 1.1 * BattleUtils.getRoomHeight(roomSpecs);
            var state = new BoxState()
                    .setPosition(BodyPosition.of(new Position().setX(x).setY(y), 0.0));
            var id = battle.getModel().getIdGenerator().generate();
            var model = new BoxModel();
            model.setId(id);
            model.setState(state);
            model.setPreCalc(preCalc);
            model.setConfig(config);
            model.setSpecs(specs);
            battle.getModel().getBoxes().put(id, model);
            battle.getModel().getUpdates().addBox(model);
            battle.setBoxDropTime(battle.getAbsoluteTime());
        }
    }

    private BoxSpecs getSpecs(boolean hasEmptyAmmo, boolean hasLowHp) {
        if (hasEmptyAmmo) {
            return BoxSpecsPreset.AMMO.getSpecs();
        }
        if (hasLowHp) {
            return BoxSpecsPreset.HP.getSpecs();
        }
        return randomSpecs();
    }

    private boolean hasEmptyAmmo(Collection<VehicleModel> vehicles) {
        return vehicles.stream()
                .map(VehicleModel::getRelativeAmmo)
                .min(Double::compare)
                .orElse(1.0) < 0.3;
    }

    private boolean hasLowHp(Collection<VehicleModel> vehicles) {
        return vehicles.stream()
                .map(VehicleModel::getRelativeHp)
                .min(Double::compare)
                .orElse(1.0) < 0.5;
    }

    private BoxSpecs randomSpecs() {
        return BoxSpecsPreset.values()[BattleUtils.generateRandom(0, BoxSpecsPreset.values().length)].getSpecs();
    }
}
