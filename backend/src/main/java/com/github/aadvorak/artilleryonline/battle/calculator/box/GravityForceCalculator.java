package com.github.aadvorak.artilleryonline.battle.calculator.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyForce;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.config.BoxConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BoxPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.BoxSpecs;
import com.github.aadvorak.artilleryonline.battle.state.BoxState;

import java.util.ArrayList;
import java.util.List;

public class GravityForceCalculator implements ForceCalculator<
        BoxSpecs,
        BoxPreCalc,
        BoxConfig,
        BoxState,
        BoxModel,
        BoxCalculations> {

    private static final String FORCE_DESCRIPTION = "Gravity";

    @Override
    public List<BodyForce> calculate(BoxCalculations calculations, BattleModel battleModel) {
        var roomGravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();

        var forces = new ArrayList<BodyForce>();

        var mass = calculations.getMass();
        forces.add(BodyForce.atCOM(
                new Force().setX(0.0).setY(-roomGravityAcceleration * mass),
                FORCE_DESCRIPTION
        ));
        return forces;
    }
}
