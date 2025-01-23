package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.statistics.StatisticsProcessor;

public class VehicleVehicleCollisionsProcessor {

    public static void process(VehicleCalculations vehicle, BattleCalculations battle) {
        var collision = VehicleVehicleCollisionsDetector.detectStrongest(vehicle, battle);
        if (collision != null) {
            vehicle.setHasCollisions(true);
            calculateAndApplyDamage(collision, battle.getModel());
            resolve(collision, battle);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(collision);
        }
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        var collision = VehicleVehicleCollisionsDetector.detectFirst(vehicle, battle);
        return collision == null;
    }

    private static void resolve(Collision collision, BattleCalculations battle) {
        recalculateVehiclesVelocities(collision);

        collision.getPair().first().getVehicleCalculations()
                .calculateNextPositionAndAngle(battle.getModel().getCurrentTimeStepSecs());
        collision.getPair().second().getVehicleCalculations()
                .calculateNextPositionAndAngle(battle.getModel().getCurrentTimeStepSecs());
        collision.getPair().first().getVehicleCalculations().recalculateWheelsVelocities();
        collision.getPair().second().getVehicleCalculations().recalculateWheelsVelocities();

        recalculateVehiclesNextPositions(collision);
        collision.getPair().second().getCollisions().add(Collision.inverted(collision));
        ((VehicleModel) collision.getPair().second().getModel()).setUpdated(true);
        collision.getPair().second().getVehicleCalculations().setHasCollisions(true);
    }

    private static void recalculateVehiclesVelocities(Collision collision) {
        var mass = collision.getPair().first().getMass();
        var otherMass = collision.getPair().second().getMass();

        var velocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().first());
        var otherVelocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().second());

        var velocityNormalProjection = velocityProjections.getNormal();
        var otherVelocityNormalProjection = otherVelocityProjections.getNormal();

        velocityProjections.setNormal(getNewVelocityNormalProjection(
                velocityNormalProjection, otherVelocityNormalProjection,
                mass, otherMass));
        collision.getPair().first().setVelocity(velocityProjections.recoverVelocity());

        otherVelocityProjections.setNormal(getNewVelocityNormalProjection(
                otherVelocityNormalProjection, velocityNormalProjection,
                otherMass, mass));
        collision.getPair().second().setVelocity(otherVelocityProjections.recoverVelocity());

        if (collision.getPair().first() instanceof WheelCalculations wheelCalculations) {
            wheelCalculations.getVehicle().recalculateVelocityByWheel(wheelCalculations);
        }
        if (collision.getPair().second() instanceof WheelCalculations wheelCalculations) {
            wheelCalculations.getVehicle().recalculateVelocityByWheel(wheelCalculations);
        }
    }

    private static void recalculateVehiclesNextPositions(Collision collision) {
        var vehicle = collision.getPair().first().getVehicleCalculations();
        var otherVehicle = collision.getPair().second().getVehicleCalculations();
        var mass = collision.getPair().first().getMass();
        var otherMass = collision.getPair().second().getMass();
        var normalMovePerMass = collision.getInterpenetration() / (mass + otherMass);
        var normalMove = normalMovePerMass * otherMass;
        var otherNormalMove = normalMovePerMass * mass;
        vehicle.applyNormalMoveToNextPosition(normalMove, collision.getAngle());
        otherVehicle.applyNormalMoveToNextPosition(- otherNormalMove, collision.getAngle());
    }

    private static double getNewVelocityNormalProjection(
            double velocityNormalProjection, double otherVelocityNormalProjection,
            double mass, double otherMass
    ) {
        return (- Math.abs(mass - otherMass) * velocityNormalProjection
                + 2 * otherMass * otherVelocityNormalProjection) / (mass + otherMass);
    }

    private static void calculateAndApplyDamage(Collision collision, BattleModel battleModel) {
        var firstModel = (VehicleModel) collision.getPair().first().getModel();
        var secondModel = (VehicleModel) collision.getPair().second().getModel();
        calculateAndApplyDamage(collision, battleModel, firstModel, secondModel);
        calculateAndApplyDamage(collision, battleModel, secondModel, firstModel);
    }

    private static void calculateAndApplyDamage(Collision collision, BattleModel battleModel,
                                                VehicleModel receiver, VehicleModel causer) {
        var minImpact = receiver.getSpecs().getMinCollisionDamageImpact();
        var impact = collision.getImpact();
        if (impact > minImpact) {
            var damage = receiver.getSpecs().getCollisionDamageCoefficient() * (impact - minImpact);
            StatisticsProcessor.increaseDamage(Math.min(damage, receiver.getState().getHitPoints()),
                    receiver.getUserId(), causer.getUserId(), battleModel);
            var hitPoints = receiver.getState().getHitPoints() - damage;
            if (hitPoints <= 0) {
                receiver.getState().setHitPoints(0.0);
                battleModel.getUpdates().removeVehicle(battleModel.getVehicleKeyById(receiver.getId()));
                if (causer.getUserId() != null) {
                    battleModel.getStatistics().get(causer.getUserId()).increaseDestroyedVehicles();
                }
            } else {
                receiver.getState().setHitPoints(hitPoints);
            }
        }
    }
}
