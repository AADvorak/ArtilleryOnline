package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class MissileGroundCollisionsDetector {

    public static Collision detectFirst(MissileCalculations missile, BattleCalculations battle) {
        var roomModel = battle.getModel().getRoom();
        if (BattleUtils.positionIsOutOfRoom(missile.getPosition(), roomModel.getSpecs())
                || BattleUtils.positionIsOutOfRoom(missile.getHeadPosition(), roomModel.getSpecs())
                || BattleUtils.positionIsOutOfRoom(missile.getTailPosition(), roomModel.getSpecs())
                || BattleUtils.positionIsUnderGround(missile.getPosition(), roomModel)
                || BattleUtils.positionIsUnderGround(missile.getHeadPosition(), roomModel)
                || BattleUtils.positionIsUnderGround(missile.getTailPosition(), roomModel)) {
            return Collision.ofMissileWithGround(missile);
        }
        return null;
    }
}
