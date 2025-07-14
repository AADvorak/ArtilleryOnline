package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.JetForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Vector;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JetForceCalculatorTest {

    private static final double SMALL_DELTA = 0.00001;
    private static final double SQRT_05 = Math.sqrt(0.5);

    private final JetForceCalculator jetForceCalculator = new JetForceCalculator();

    @Test
    public void horizontalJetOffMovingRight() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().setMovingDirection(MovingDirection.RIGHT);
        var battleModel = new BattleModel();
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        var jetForces = jetForceCalculator.calculate(vehicleCalculations, battleModel);
        assertEquals(0, jetForces.size());
    }

    @Test
    public void horizontalJetOnNoFuel() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getJetState().setActive(true);
        vehicleModel.getState().setMovingDirection(MovingDirection.RIGHT);
        vehicleModel.getState().getJetState().setVolume(0.0);
        var battleModel = new BattleModel();
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        var jetForces = jetForceCalculator.calculate(vehicleCalculations, battleModel);
        assertEquals(0, jetForces.size());
    }

    @Test
    public void horizontalJetOnNoMovingDirection() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getJetState().setActive(true);
        var battleModel = new BattleModel();
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        var jetForces = jetForceCalculator.calculate(vehicleCalculations, battleModel);
        assertEquals(0, jetForces.size());
    }

    @Test
    public void horizontalJetOnMovingRight() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getJetState().setActive(true);
        vehicleModel.getState().setMovingDirection(MovingDirection.RIGHT);
        var battleModel = new BattleModel();
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        var jetForces = jetForceCalculator.calculate(vehicleCalculations, battleModel);
        assertEquals(1, jetForces.size());
        var jetForce = jetForces.get(0);
        var angle = JetForceCalculator.HORIZONTAL_JET_ANGLE;
        var magnitude = vehicleModel.getConfig().getJet().getAcceleration() * vehicleModel.getPreCalc().getMass();
        assertAll(
                () -> assertEquals(magnitude * Math.cos(angle), jetForce.moving().getX(), SMALL_DELTA),
                () -> assertEquals(magnitude * Math.sin(angle), jetForce.moving().getY(), SMALL_DELTA),
                () -> assertNull(jetForce.rotating()),
                () -> assertNull(jetForce.radiusVector())
        );
    }

    @Test
    public void horizontalJetOnMovingLeft() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().getJetState().setActive(true);
        vehicleModel.getState().setMovingDirection(MovingDirection.LEFT);
        var battleModel = new BattleModel();
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        var jetForces = jetForceCalculator.calculate(vehicleCalculations, battleModel);
        assertEquals(1, jetForces.size());
        var jetForce = jetForces.get(0);
        var angle = JetForceCalculator.HORIZONTAL_JET_ANGLE;
        var magnitude = vehicleModel.getConfig().getJet().getAcceleration() * vehicleModel.getPreCalc().getMass();
        assertAll(
                () -> assertEquals(-magnitude * Math.cos(angle), jetForce.moving().getX(), SMALL_DELTA),
                () -> assertEquals(magnitude * Math.sin(angle), jetForce.moving().getY(), SMALL_DELTA),
                () -> assertNull(jetForce.rotating()),
                () -> assertNull(jetForce.radiusVector())
        );
    }

    @Test
    public void verticalJetOnNoMovingDirection() {
        var vehicleModel = TestVehicleGenerator.generate("Light");
        vehicleModel.getState().getJetState().setActive(true);
        var battleModel = new BattleModel();
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        var jetForces = jetForceCalculator.calculate(vehicleCalculations, battleModel);
        assertEquals(2, jetForces.size());
        var jetForce1 = jetForces.get(0);
        var jetForce2 = jetForces.get(1);
        var expectedMagnitude = vehicleModel.getConfig().getJet().getAcceleration() * vehicleModel.getPreCalc().getMass();
        var sumForce = Vector.sumOf(jetForce1.moving(), jetForce1.rotating(), jetForce2.moving(), jetForce2.rotating());
        assertAll(
                () -> assertEquals(expectedMagnitude, sumForce.getY(), SMALL_DELTA),
                () -> assertEquals(0, sumForce.getX(), SMALL_DELTA),
                () -> assertEquals(vehicleModel.getPreCalc().getWheelDistance(), jetForce1.radiusVector().magnitude()),
                () -> assertEquals(vehicleModel.getPreCalc().getWheelDistance(), jetForce2.radiusVector().magnitude())
        );
    }

    @Test
    public void verticalJetOnMovingRight() {
        var vehicleModel = TestVehicleGenerator.generate("Light");
        vehicleModel.getState().setMovingDirection(MovingDirection.RIGHT);
        vehicleModel.getState().getJetState().setActive(true);
        var battleModel = new BattleModel();
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        var jetForces = jetForceCalculator.calculate(vehicleCalculations, battleModel);
        assertEquals(2, jetForces.size());
        var jetForce1 = jetForces.get(0);
        var jetForce2 = jetForces.get(1);
        var expectedMagnitude = vehicleModel.getConfig().getJet().getAcceleration() * vehicleModel.getPreCalc().getMass();
        var sumForce = Vector.sumOf(jetForce1.moving(), jetForce1.rotating(), jetForce2.moving(), jetForce2.rotating());
        assertAll(
                () -> assertEquals(SQRT_05 * expectedMagnitude, sumForce.getY(), SMALL_DELTA),
                () -> assertEquals(SQRT_05 * expectedMagnitude, sumForce.getX(), SMALL_DELTA),
                () -> assertEquals(vehicleModel.getPreCalc().getWheelDistance(), jetForce1.radiusVector().magnitude()),
                () -> assertEquals(vehicleModel.getPreCalc().getWheelDistance(), jetForce2.radiusVector().magnitude())
        );
    }

    @Test
    public void verticalJetOnMovingLeft() {
        var vehicleModel = TestVehicleGenerator.generate("Light");
        vehicleModel.getState().setMovingDirection(MovingDirection.LEFT);
        vehicleModel.getState().getJetState().setActive(true);
        var battleModel = new BattleModel();
        var vehicleCalculations = new VehicleCalculations(vehicleModel);
        var jetForces = jetForceCalculator.calculate(vehicleCalculations, battleModel);
        assertEquals(2, jetForces.size());
        var jetForce1 = jetForces.get(0);
        var jetForce2 = jetForces.get(1);
        var expectedMagnitude = vehicleModel.getConfig().getJet().getAcceleration() * vehicleModel.getPreCalc().getMass();
        var sumForce = Vector.sumOf(jetForce1.moving(), jetForce1.rotating(), jetForce2.moving(), jetForce2.rotating());
        assertAll(
                () -> assertEquals(SQRT_05 * expectedMagnitude, sumForce.getY(), SMALL_DELTA),
                () -> assertEquals(- SQRT_05 * expectedMagnitude, sumForce.getX(), SMALL_DELTA),
                () -> assertEquals(vehicleModel.getPreCalc().getWheelDistance(), jetForce1.radiusVector().magnitude()),
                () -> assertEquals(vehicleModel.getPreCalc().getWheelDistance(), jetForce2.radiusVector().magnitude())
        );
    }
}
