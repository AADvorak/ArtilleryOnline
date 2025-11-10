package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.GroundFrictionForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.Vector;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleGroundFrictionForceCalculatorTest {

    private static final double SMALL_DELTA = 0.00001;

    private final GroundFrictionForceCalculator calculator = new GroundFrictionForceCalculator();

    @Test
    public void wheelsInGroundNoMove() {
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
        var forces = calculator.calculate(vehicleCalculations, battleModel);
        assertAll(
                () -> assertEquals(0, forces.size()),
                () -> assertEquals(2, vehicleCalculations.getGroundContacts().size())
        );
    }

    @Test
    public void wheelsInGroundMovingX() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getPosition().setX(10.0);
        vehicleModel.getState().getVelocity().setX(1.0);
        vehicleModel.getState().getPosition().setY(TestRoomGenerator.GROUND_LEVEL
                + vehicleModel.getSpecs().getWheelRadius() * 2 - SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD);
        vehicleModel.getState().setPosition(vehicleModel.getState().getPosition()
                .shifted(vehicleModel.getPreCalc().getCenterOfMassShift()));
        var roomModel = TestRoomGenerator.generate();
        var battleModel = new BattleModel().setRoom(roomModel);
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        vehicleCalculations.calculateAllGroundContacts(roomModel);
        var forces = calculator.calculate(vehicleCalculations, battleModel);
        var sumForces = forces.stream()
                .map(bodyForce -> Vector.sumOf(bodyForce.moving(), bodyForce.rotating()))
                .toList();
        assertAll(
                () -> assertEquals(2, forces.size()),
                () -> assertFalse(sumForces.stream().anyMatch(force -> force.getX() > 0)),
                () -> assertFalse(sumForces.stream().anyMatch(force -> Math.abs(force.getY()) > 0)),
                () -> assertEquals(2, vehicleCalculations.getGroundContacts().size())
        );
    }

    @Test
    public void fullOverGroundNoMove() {
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
        var forces = calculator.calculate(vehicleCalculations, battleModel);
        assertAll(
                () -> assertEquals(0, forces.size()),
                () -> assertEquals(0, vehicleCalculations.getGroundContacts().size())
        );
    }

    @Test
    public void fullOverGroundMovingX() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getPosition().setX(10.0);
        vehicleModel.getState().getVelocity().setX(1.0);
        vehicleModel.getState().getPosition().setY(TestRoomGenerator.GROUND_LEVEL
                + vehicleModel.getSpecs().getWheelRadius() * 2 + SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD);
        vehicleModel.getState().setPosition(vehicleModel.getState().getPosition()
                .shifted(vehicleModel.getPreCalc().getCenterOfMassShift()));
        var roomModel = TestRoomGenerator.generate();
        var battleModel = new BattleModel().setRoom(roomModel);
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        vehicleCalculations.calculateAllGroundContacts(roomModel);
        var forces = calculator.calculate(vehicleCalculations, battleModel);
        assertAll(
                () -> assertEquals(0, forces.size()),
                () -> assertEquals(0, vehicleCalculations.getGroundContacts().size())
        );
    }

    @Test
    public void oneWheelOverGroundNoMove() {
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
        var forces = calculator.calculate(vehicleCalculations, battleModel);
        assertAll(
                () -> assertEquals(0, forces.size()),
                () -> assertEquals(1, vehicleCalculations.getGroundContacts().size())
        );
    }

    @Test
    public void oneWheelOverGroundMovingX() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getPosition().setX(10.0);
        vehicleModel.getState().getVelocity().setX(1.0);
        vehicleModel.getState().getPosition().setY(TestRoomGenerator.GROUND_LEVEL
                + vehicleModel.getSpecs().getWheelRadius() * 2 - Constants.INTERPENETRATION_THRESHOLD);
        vehicleModel.getState().setPosition(vehicleModel.getState().getPosition()
                .shifted(vehicleModel.getPreCalc().getCenterOfMassShift()));
        vehicleModel.getState().getPosition().setAngle(Math.PI / 16);
        var roomModel = TestRoomGenerator.generate();
        var battleModel = new BattleModel().setRoom(roomModel);
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        vehicleCalculations.calculateAllGroundContacts(roomModel);
        var forces = calculator.calculate(vehicleCalculations, battleModel);
        var sumForces = forces.stream()
                .map(bodyForce -> Vector.sumOf(bodyForce.moving(), bodyForce.rotating()))
                .toList();
        assertAll(
                () -> assertEquals(1, forces.size()),
                () -> assertFalse(sumForces.stream().anyMatch(force -> force.getX() > 0)),
                () -> assertFalse(sumForces.stream().anyMatch(force -> Math.abs(force.getY()) > 0)),
                () -> assertEquals(1, vehicleCalculations.getGroundContacts().size())
        );
    }
}
