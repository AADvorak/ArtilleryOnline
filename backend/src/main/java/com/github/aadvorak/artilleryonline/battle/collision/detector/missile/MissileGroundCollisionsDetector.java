package com.github.aadvorak.artilleryonline.battle.collision.detector.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

@Component
public class MissileGroundCollisionsDetector extends MissileCollisionsDetectorBase {

    protected Collision detectFirst(MissileCalculations missile, BattleCalculations battle) {
        var roomModel = battle.getModel().getRoom();
        if (BattleUtils.positionIsOutOfRoom(missile.getPosition(), roomModel.getSpecs())
                || BattleUtils.positionIsOutOfRoom(missile.getPositions().getHead(), roomModel.getSpecs())
                || BattleUtils.positionIsOutOfRoom(missile.getPositions().getTail(), roomModel.getSpecs())
                || BattleUtils.positionIsUnderGround(missile.getPosition(), roomModel)
                || BattleUtils.positionIsUnderGround(missile.getPositions().getHead(), roomModel)
                || BattleUtils.positionIsUnderGround(missile.getPositions().getTail(), roomModel)) {
            var normal = missile.getPositions().getTail().vectorTo(missile.getPositions().getHead()).normalized();
            return Collision.withGround(missile, Contact.withUncheckedDepthOf(0.0, normal, missile.getPosition()));
        }
        return null;
    }
}
