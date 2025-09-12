package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.common.GravityForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GravityForceCalculatorTest {

    private static final double SMALL_DELTA = 0.00001;

    private final GravityForceCalculator gravityForceCalculator = new GravityForceCalculator();

    @Test
    public void fullUnderGround() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getPosition().setX(10.0);
        var roomModel = TestRoomGenerator.generate();
        var battleModel = new BattleModel().setRoom(roomModel);
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        vehicleCalculations.calculateAllGroundContacts(roomModel);
        var gravityForces = gravityForceCalculator.calculate(vehicleCalculations, battleModel);
        assertAll(
                () -> assertEquals(0, gravityForces.size()),
                () -> assertTrue(vehicleCalculations.getGroundContacts().size() > 2)
        );
    }

    @Test
    public void wheelsInGround() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getPosition().setX(10.0);
        vehicleModel.getState().getPosition().setY(TestRoomGenerator.GROUND_LEVEL
                + vehicleModel.getSpecs().getWheelRadius() * 2 - SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD);
        vehicleModel.getState().setPosition(vehicleModel.getState().getPosition()
                .shifted(vehicleModel.getPreCalc().getCenterOfMassShift()));
        var roomModel = TestRoomGenerator.generate();
        var battleModel = new BattleModel().setRoom(roomModel);
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        vehicleCalculations.calculateAllGroundContacts(roomModel);
        var gravityForces = gravityForceCalculator.calculate(vehicleCalculations, battleModel);
        assertAll(
                () -> assertEquals(0, gravityForces.size()),
                () -> assertEquals(2, vehicleCalculations.getGroundContacts().size())
        );
    }

    @Test
    public void fullOverGround() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getPosition().setX(10.0);
        vehicleModel.getState().getPosition().setY(TestRoomGenerator.GROUND_LEVEL
                + vehicleModel.getSpecs().getWheelRadius() * 2 + SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD);
        vehicleModel.getState().setPosition(vehicleModel.getState().getPosition()
                .shifted(vehicleModel.getPreCalc().getCenterOfMassShift()));
        var roomModel = TestRoomGenerator.generate();
        var battleModel = new BattleModel().setRoom(roomModel);
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        vehicleCalculations.calculateAllGroundContacts(roomModel);
        var gravityForces = gravityForceCalculator.calculate(vehicleCalculations, battleModel);
        assertNotNull(gravityForces);
        assertEquals(1, gravityForces.size());
        var gravityForce = gravityForces.get(0);
        var expectedY = - vehicleCalculations.getMass() * roomModel.getSpecs().getGravityAcceleration();
        assertAll(
                () -> assertEquals(0.0, gravityForce.moving().getX(), SMALL_DELTA),
                () -> assertEquals(expectedY, gravityForce.moving().getY(), SMALL_DELTA),
                () -> assertNull(gravityForce.rotating()),
                () -> assertNull(gravityForce.radiusVector()),
                () -> assertEquals(0, vehicleCalculations.getGroundContacts().size())
        );
    }

    @Test
    public void oneWheelOverGround() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getPosition().setX(10.0);
        vehicleModel.getState().getPosition().setY(TestRoomGenerator.GROUND_LEVEL
                + vehicleModel.getSpecs().getWheelRadius() * 2 - Constants.INTERPENETRATION_THRESHOLD);
        vehicleModel.getState().setPosition(vehicleModel.getState().getPosition()
                .shifted(vehicleModel.getPreCalc().getCenterOfMassShift()));
        vehicleModel.getState().getPosition().setAngle(Math.PI / 16);
        var roomModel = TestRoomGenerator.generate();
        var battleModel = new BattleModel().setRoom(roomModel);
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        vehicleCalculations.calculateAllGroundContacts(roomModel);
        var gravityForces = gravityForceCalculator.calculate(vehicleCalculations, battleModel);
        assertNotNull(gravityForces);
        assertEquals(1, gravityForces.size());
        var gravityForce = gravityForces.get(0);
        var expectedY = - vehicleCalculations.getMass() * roomModel.getSpecs().getGravityAcceleration();
        assertAll(
                () -> assertEquals(0.0, gravityForce.moving().getX(), SMALL_DELTA),
                () -> assertEquals(expectedY, gravityForce.moving().getY(), SMALL_DELTA),
                () -> assertNull(gravityForce.rotating()),
                () -> assertNull(gravityForce.radiusVector()),
                () -> assertEquals(1, vehicleCalculations.getGroundContacts().size())
        );
    }
}
