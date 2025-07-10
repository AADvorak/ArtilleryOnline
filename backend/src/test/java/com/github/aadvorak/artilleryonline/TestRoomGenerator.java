package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.config.RoomConfig;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.preset.RoomSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.state.RoomState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

import java.util.ArrayList;
import java.util.List;

public class TestRoomGenerator {

    public static final double GROUND_LEVEL = 1.0;

    public static RoomModel generate() {
        var roomModel = new RoomModel();
        var specs = RoomSpecsPreset.DEFAULT.getSpecs();
        var state = new RoomState()
                .setGroundLine(createGroundLine(specs));
        var config = new RoomConfig()
                .setBackground(BattleUtils.generateRandom(1, 7))
                .setGroundTexture(BattleUtils.generateRandom(1, 6));
        roomModel.setSpecs(specs);
        roomModel.setState(state);
        roomModel.setConfig(config);
        return roomModel;
    }

    private static List<Double> createGroundLine(RoomSpecs roomSpecs) {
        var groundLine = new ArrayList<Double>();
        var xMin = roomSpecs.getLeftBottom().getX();
        var xMax = roomSpecs.getRightTop().getX();
        for (var x = xMin; x <= xMax; x += roomSpecs.getStep()) {
            groundLine.add(GROUND_LEVEL);
        }
        return groundLine;
    }
}
