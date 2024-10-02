package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleGroundCollideProcessor {

    public static boolean processCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                         BattleModel battleModel) {
        var groundCollideWheel = getGroundCollideWheel(calculations, vehicleModel, battleModel);
        if (groundCollideWheel != null) {
            doCollide(vehicleModel, calculations, groundCollideWheel);
            vehicleModel.setCollided(true);
            battleModel.setUpdated(true);
            return true;
        }
        return false;
    }

    private static WheelCalculations getGroundCollideWheel(VehicleCalculations calculations, VehicleModel vehicleModel,
                                                           BattleModel battleModel) {
        var nextRightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, calculations.getNextPosition(),
                calculations.getNextAngle());
        var nextLeftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, calculations.getNextPosition(),
                calculations.getNextAngle());
        var rightWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextRightWheelPosition.getX(),
                battleModel.getRoom());
        var leftWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextLeftWheelPosition.getX(),
                battleModel.getRoom());
        if (rightWheelNearestGroundPoint.getY() >= nextRightWheelPosition.getY()) {
            /*System.out.printf("Collision RW. ID: %d, X: %.3f, Y: %.3f, NextX: %.3f, NextY: %.3f, GPx: %.3f, GPy: %.3f %n",
                    vehicleModel.getId(),
                    calculations.getRightWheel().getPosition().getX(),
                    calculations.getRightWheel().getPosition().getY(),
                    nextRightWheelPosition.getX(),
                    nextRightWheelPosition.getY(),
                    rightWheelNearestGroundPoint.getX(),
                    rightWheelNearestGroundPoint.getY()
            );*/
            return calculations.getRightWheel();
        }
        if (leftWheelNearestGroundPoint.getY() >= nextLeftWheelPosition.getY()) {
            /*System.out.printf("Collision LW. ID: %d, X: %.3f, Y: %.3f, NextX: %.3f, NextY: %.3f, GPx: %.3f, GPy: %.3f %n",
                    vehicleModel.getId(),
                    calculations.getLeftWheel().getPosition().getX(),
                    calculations.getLeftWheel().getPosition().getY(),
                    nextLeftWheelPosition.getX(),
                    nextLeftWheelPosition.getY(),
                    leftWheelNearestGroundPoint.getX(),
                    leftWheelNearestGroundPoint.getY()
            );*/
            return calculations.getLeftWheel();
        }
        return null;
    }

    public static void doCollide(VehicleModel vehicleModel, VehicleCalculations calculations,
                                  WheelCalculations wheelCalculations) {
        VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.getLeftWheel());

        var groundAngle = wheelCalculations.getGroundAngle();
        var wheelVelocity = wheelCalculations.getVelocity();
        var velocityVerticalProjection = - 2.0 * VectorUtils.getVerticalProjection(wheelVelocity, groundAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(wheelVelocity, groundAngle);
        /*System.out.printf("Before recalc. Vx: %.3f, Vy: %.3f, Va: %.3f, Vwx: %.3f, Vwy: %.3f, Vv: %.3f, Vh: %.3f %n",
                vehicleModel.getState().getVehicleVelocity().getX(),
                vehicleModel.getState().getVehicleVelocity().getY(),
                vehicleModel.getState().getVehicleVelocity().getAngle(),
                wheelVelocity.getX(),
                wheelVelocity.getY(),
                velocityVerticalProjection,
                velocityHorizontalProjection
        );*/

        wheelVelocity.setX(VectorUtils.getComponentX(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));
        wheelVelocity.setY(VectorUtils.getComponentY(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));

        var rightWheelVelocity = calculations.getRightWheel().getVelocity();
        var leftWheelVelocity = calculations.getLeftWheel().getVelocity();
        var rightWheelSign = calculations.getRightWheel().getSign().getValue();
        var leftWheelSign = calculations.getLeftWheel().getSign().getValue();
        var angle = vehicleModel.getState().getAngle();

        var angleVelocity = - (rightWheelVelocity.getY() * rightWheelSign + leftWheelVelocity.getY() * leftWheelSign)
                / (2 * Math.cos(angle));
        var wheelSign = wheelCalculations.getSign().getValue();
        vehicleModel.getState().getVehicleVelocity()
                .setAngle(angleVelocity)
                .setX(wheelVelocity.getX() - wheelSign * angleVelocity * Math.sin(angle))
                .setY(wheelVelocity.getY() + wheelSign * angleVelocity * Math.cos(angle));

        /*System.out.printf("After recalc. Vx: %.3f, Vy: %.3f, Va: %.3f, Vwx: %.3f, Vwy: %.3f, Vv: %.3f, Vh: %.3f %n",
                vehicleModel.getState().getVehicleVelocity().getX(),
                vehicleModel.getState().getVehicleVelocity().getY(),
                vehicleModel.getState().getVehicleVelocity().getAngle(),
                wheelVelocity.getX(),
                wheelVelocity.getY(),
                velocityVerticalProjection,
                velocityHorizontalProjection
        );*/
    }
}
