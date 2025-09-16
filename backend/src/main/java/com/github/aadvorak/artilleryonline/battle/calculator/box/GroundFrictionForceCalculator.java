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

public class GroundFrictionForceCalculator implements ForceCalculator<
        BoxSpecs,
        BoxPreCalc,
        BoxConfig,
        BoxState,
        BoxModel,
        BoxCalculations> {

    private static final String FORCE_DESCRIPTION = "Ground Friction";

    @Override
    public List<BodyForce> calculate(BoxCalculations calculations, BattleModel battleModel) {
        var groundFrictionCoefficient = battleModel.getRoom().getSpecs().getGroundFrictionCoefficient();
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        var contactsNumber = calculations.getGroundContacts().size();
        var forces = new ArrayList<BodyForce>();
        if (calculations.getGroundContacts() != null) {
            var forceMultiplier = groundFrictionCoefficient * gravityAcceleration
                    * calculations.getMass() / contactsNumber;
            calculations.getGroundContacts().forEach(contact -> ForceCalculatorUtils.addFriction(forces,
                    calculations, contact, forceMultiplier, FORCE_DESCRIPTION));
        }
        return forces;
    }
}
