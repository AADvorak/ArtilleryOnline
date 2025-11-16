package com.github.aadvorak.artilleryonline.battle.calculator.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.TargetCalculator;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.specs.MissileSpecs;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

import java.util.stream.Collectors;

public class CorrectingAccelerationCalculator {

    private static final double PRECISION_THRESHOLD = 6.0;

    private static final double CLOSE_EDGE_DISTANCE = 5.0;

    private static final double CLOSE_EDGE_ANGLE = - Math.PI / 4;

    private static final double FAR_EDGE_DISTANCE = 10.0;

    private static final double FAR_EDGE_ANGLE = Math.PI / 5;

    private static final double K = (FAR_EDGE_ANGLE - CLOSE_EDGE_ANGLE) / (FAR_EDGE_DISTANCE - CLOSE_EDGE_DISTANCE);

    private static final double B = CLOSE_EDGE_ANGLE - CLOSE_EDGE_DISTANCE * K;

    public static double calculate(MissileCalculations calculations, BattleModel battleModel) {
        var missileState = calculations.getModel().getState();
        var missileSpecs = calculations.getModel().getSpecs();
        var velocityMagnitude = missileState.getVelocity().getMovingVelocity().magnitude();
        var correctingVelocity = velocityMagnitude - missileSpecs.getMinCorrectingVelocity();
        var missilePosition = missileState.getPosition();
        if (correctingVelocity <= 0) {
            return getAcceleration(missilePosition.getAngle(), Math.PI / 2, missileSpecs, velocityMagnitude);
        }
        var targets = TargetCalculator.calculatePositions(calculations.getModel().getVehicleId(), battleModel);
        if (targets.isEmpty()) {
            return 0.0;
        }
        var targetDataSet = targets.stream()
                .map(position -> new TargetData(missilePosition.getCenter().angleTo(position),
                        missilePosition.getCenter().distanceTo(position),
                        position.getX() - missilePosition.getX()))
                .collect(Collectors.toSet());
        var iterator = targetDataSet.iterator();
        var closestTarget = iterator.next();
        while (iterator.hasNext()) {
            var targetData = iterator.next();
            if (Math.abs(targetData.xDistance) < Math.abs(closestTarget.xDistance)) {
                closestTarget = targetData;
            }
        }
        var absXDistance = Math.abs(closestTarget.xDistance);
        if (absXDistance > FAR_EDGE_DISTANCE) {
            var tagetAngle = closestTarget.xDistance > 0 ? FAR_EDGE_ANGLE : Math.PI - FAR_EDGE_ANGLE;
            return getAcceleration(missilePosition.getAngle(), tagetAngle, missileSpecs, velocityMagnitude);
        }
        if (absXDistance > CLOSE_EDGE_DISTANCE) {
            var angle = K * absXDistance + B;
            var tagetAngle = closestTarget.xDistance > 0 ? angle : Math.PI - angle;
            return getAcceleration(missilePosition.getAngle(), tagetAngle, missileSpecs, velocityMagnitude);
        }
        var angleDiff = GeometryUtils.calculateAngleDiff(missilePosition.getAngle(), closestTarget.angle);
        if (Math.abs(angleDiff) < missileSpecs.getAnglePrecision()
                * getAnglePrecisionCoefficient(closestTarget.distance)) {
            return 0.0;
        }
        return Math.signum(angleDiff) * correctingVelocity
                * missileSpecs.getCorrectingAccelerationCoefficient();
    }

    private static double getAcceleration(double missileAngle, double targetAngle,
                                          MissileSpecs missileSpecs, double velocityMagnitude) {
        return Math.signum(GeometryUtils.calculateAngleDiff(missileAngle, targetAngle)) * velocityMagnitude
                * missileSpecs.getCorrectingAccelerationCoefficient() / missileSpecs.getMinCorrectingVelocity();
    }

    private static double getAnglePrecisionCoefficient(double distance) {
        if (distance < PRECISION_THRESHOLD) {
            return distance /  PRECISION_THRESHOLD;
        }
        return 1.0;
    }

    private record TargetData(double angle, double distance, double xDistance) {
    }
}
