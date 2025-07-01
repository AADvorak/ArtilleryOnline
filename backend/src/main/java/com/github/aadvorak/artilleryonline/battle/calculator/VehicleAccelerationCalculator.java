package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.*;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;

import java.util.List;

public class VehicleAccelerationCalculator {

    private static final List<
            ForceCalculator<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations>
            > forceCalculators = List.of(
            new GravityForceCalculator(),
            new GroundFrictionForceCalculator(),
            new GroundReactionForceCalculator(),
            new JetForceCalculator(),
            new EngineForceCalculator()
    );

    private static final BodyAccelerationCalculator<
            VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
            > calculator = new BodyAccelerationCalculator<>(forceCalculators);

    public static BodyAcceleration getVehicleAcceleration(VehicleCalculations vehicle, BattleModel battleModel) {
        vehicle.recalculateWheelsVelocities();

        calculateGroundContact(vehicle.getRightWheel(), battleModel.getRoom());
        calculateGroundContact(vehicle.getLeftWheel(), battleModel.getRoom());
        calculateGroundContacts(vehicle, battleModel.getRoom());

        return calculator.calculate(vehicle, battleModel);
    }

    private static void calculateGroundContact(WheelCalculations wheel, RoomModel roomModel) {
        wheel.setGroundContact(GroundContactUtils.getGroundContact(
                new Circle(wheel.getPosition(), wheel.getModel().getSpecs().getWheelRadius()),
                roomModel, false));
    }

    private static void calculateGroundContacts(VehicleCalculations vehicle, RoomModel roomModel) {
        var bodyPosition = BodyPosition.of(vehicle.getGeometryPosition(),
                vehicle.getModel().getState().getPosition().getAngle());
        var bodyPart = BodyPart.of(bodyPosition, vehicle.getModel().getSpecs().getTurretShape());
        vehicle.setGroundContacts(GroundContactUtils.getGroundContacts(bodyPart, roomModel, false));
    }
}
