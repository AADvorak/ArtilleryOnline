package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.ForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.*;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.*;
import com.github.aadvorak.artilleryonline.battle.common.*;
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
            new AirFrictionForceCalculator(),
            new EngineForceCalculator(),
            new GravityForceCalculator(),
            new GroundFrictionForceCalculator(),
            new GroundReactionForceCalculator(),
            new JetForceCalculator()
    );

    private static final BodyAccelerationCalculator<
            VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
            > calculator = new BodyAccelerationCalculator<>(forceCalculators);

    public static BodyAcceleration getVehicleAcceleration(VehicleCalculations vehicle, BattleModel battleModel) {
        vehicle.recalculateWheelsVelocities();

        calculateGroundContactAndState(vehicle.getRightWheel(), battleModel.getRoom());
        calculateGroundContactAndState(vehicle.getLeftWheel(), battleModel.getRoom());
        calculateGroundContact(vehicle, battleModel.getRoom());

        var acceleration = calculator.calculate(vehicle, battleModel);
        var angleVelocity = vehicle.getModel().getState().getVelocity().getAngle();
        acceleration.setAngle(acceleration.getAngle() - angleVelocity);

        return acceleration;
    }

    private static void calculateGroundContactAndState(WheelCalculations wheel, RoomModel roomModel) {
        wheel.setGroundContact(GroundContactUtils.getGroundContact(
                new Circle(wheel.getPosition(), wheel.getModel().getSpecs().getWheelRadius()),
                roomModel, false));
        GroundStateCalculator.calculate(wheel);
    }

    private static void calculateGroundContact(VehicleCalculations vehicle, RoomModel roomModel) {
        var position = vehicle.getGeometryPosition();
        var angle = vehicle.getModel().getState().getPosition().getAngle();
        var contact = GroundContactUtils.getGroundContact(
                new Circle(position, vehicle.getModel().getSpecs().getRadius()),
                roomModel, false);
        if (contact == null) {
            return;
        }
        var hullVector = position.vectorTo(position.shifted(1.0,angle + Math.PI / 2));
        if (hullVector.dotProduct(contact.normal()) < 0) {
            return;
        }
        vehicle.setGroundContact(contact);
    }
}
