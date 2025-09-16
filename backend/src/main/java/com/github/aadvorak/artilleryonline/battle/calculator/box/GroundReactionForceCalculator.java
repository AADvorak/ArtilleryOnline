package com.github.aadvorak.artilleryonline.battle.calculator.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyForce;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.utils.ForceCalculatorUtils;
import com.github.aadvorak.artilleryonline.battle.config.BoxConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BoxPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.BoxSpecs;
import com.github.aadvorak.artilleryonline.battle.state.BoxState;

import java.util.ArrayList;
import java.util.List;

public class GroundReactionForceCalculator implements ForceCalculator<
        BoxSpecs,
        BoxPreCalc,
        BoxConfig,
        BoxState,
        BoxModel,
        BoxCalculations> {

    private static final String FORCE_DESCRIPTION = "Ground reaction";

    @Override
    public List<BodyForce> calculate(BoxCalculations calculations, BattleModel battleModel) {
        var groundReactionCoefficient = battleModel.getRoom().getSpecs().getGroundReactionCoefficient();
        var groundMaxDepth = battleModel.getRoom().getSpecs().getGroundMaxDepth();
        var forces = new ArrayList<BodyForce>();
        if (calculations.getGroundContacts() != null) {
            calculations.getGroundContacts().forEach(contact -> ForceCalculatorUtils.addReaction(forces,
                    calculations, contact, groundMaxDepth, groundReactionCoefficient, FORCE_DESCRIPTION));
        }
        return forces;
    }
}
