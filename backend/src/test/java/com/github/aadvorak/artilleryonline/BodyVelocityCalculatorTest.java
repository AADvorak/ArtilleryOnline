package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.BodyAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.BodyVelocityCalculator;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BodyVelocityCalculatorTest {

    private static final BodyAccelerationCalculator<
            VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
            > accelerationCalculator = new BodyAccelerationCalculator<>(new ArrayList<>(), new ArrayList<>());

    private static final BodyVelocityCalculator<
            VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
            > velocityCalculator = new BodyVelocityCalculator<>(accelerationCalculator);

    @Test
    public void testFriction() {
        var values = List.of(-0.1, -1.0, -10.0, -50.0, -100.0, 0.1, 1.0, 10.0, 50.0, 100.0);
        values.forEach(this::testFriction);
    }

    private void testFriction(double value) {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        var velocity = vehicleModel.getState().getVelocity();
        velocity.setX(value);
        velocity.setY(value);
        velocity.setAngle(value);
        var roomModel = TestRoomGenerator.generate();
        var battleModel = new BattleModel().setRoom(roomModel);
        battleModel.setCurrentTimeStepSecs(0.01);
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        velocityCalculator.recalculateVelocity(vehicleCalculations, battleModel);
        assertAll(
                () -> assertTrue(Math.abs(velocity.getX()) < Math.abs(value) && value * velocity.getX() > 0),
                () -> assertTrue(Math.abs(velocity.getY()) < Math.abs(value) && value * velocity.getY() > 0),
                () -> assertTrue(Math.abs(velocity.getAngle()) < Math.abs(value) && value * velocity.getAngle() > 0)
        );
    }
}
