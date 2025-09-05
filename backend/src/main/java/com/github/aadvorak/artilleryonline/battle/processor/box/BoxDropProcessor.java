package com.github.aadvorak.artilleryonline.battle.processor.box;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BoxPreCalc;
import com.github.aadvorak.artilleryonline.battle.preset.BoxSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.state.BoxState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class BoxDropProcessor {

    private static final long DROP_DELAY = 1000;
    private static final int MAX_BOXES = 20;

    public static void drop(Battle battle) {
        if (battle.getModel().getBoxes().size() >= MAX_BOXES
                || battle.getAbsoluteTime() < battle.getBoxDropTime() + DROP_DELAY) {
            return;
        }
        var roomSpecs = battle.getModel().getRoom().getSpecs();
        var specs = BoxSpecsPreset.values()[BattleUtils.generateRandom(0, BoxSpecsPreset.values().length)].getSpecs();
        var preCalc = new BoxPreCalc(specs);
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
        model.setSpecs(specs);
        battle.getModel().getBoxes().put(id, model);
        battle.getModel().getUpdates().addBox(model);
        battle.setBoxDropTime(battle.getAbsoluteTime());
    }
}
