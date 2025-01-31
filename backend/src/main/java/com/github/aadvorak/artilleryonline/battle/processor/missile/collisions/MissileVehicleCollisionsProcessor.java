package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;

public class MissileVehicleCollisionsProcessor {

    public static void process(MissileCalculations missile, BattleCalculations battle) {
        var collision = MissileVehicleCollisionsDetector.detectHead(missile, battle);
        if (collision != null) {
            var hitObject = collision.getPair().second();
            ((VehicleModel) hitObject.getModel()).setUpdated(true);
            missile.getCollisions().add(collision);
            if (hitObject instanceof VehicleCalculations vehicle) {
                DamageProcessor.processHitVehicle(vehicle, missile, battle);
            }
            if (hitObject instanceof WheelCalculations wheel) {
                // todo wheel
            }
            pushVehicle(collision);
        } else {
            collision = MissileVehicleCollisionsDetector.detectBody(missile, battle);
            if (collision != null) {
                missile.getCollisions().add(collision);
                DamageProcessor.processHit(missile, battle);
            }
        }
    }

    private static void pushVehicle(Collision collision) {
        var missileMass = collision.getPair().first().getMass();
        var vehicleMass = collision.getPair().second().getMass();
        // todo common method
        if (collision.getPair().second() instanceof VehicleCalculations vehicle) {
            var vehicleVelocitiesProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().second());
            var missileVelocitiesProjections = collision.getVelocitiesProjections().first();
            vehicleVelocitiesProjections.setNormal(vehicleVelocitiesProjections.getNormal()
                    + missileMass * missileVelocitiesProjections.getNormal() / vehicleMass);
            vehicle.setVelocity(vehicleVelocitiesProjections.recoverVelocity());

            var vehicleVelocity = vehicle.getModel().getState().getVelocity();
            var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
            vehicleVelocity.setAngle(vehicleVelocity.getAngle()
                    + missileVelocitiesProjections.getTangential() * (missileMass / vehicleMass) / vehicleRadius);
        }
        if (collision.getPair().second() instanceof WheelCalculations wheel) {
            var wheelVelocity = wheel.getVelocity();
            var shellVelocity = collision.getPair().first().getVelocity();
            wheelVelocity.setX(wheelVelocity.getX() + missileMass * shellVelocity.getX() / vehicleMass);
            wheelVelocity.setY(wheelVelocity.getY() + missileMass * shellVelocity.getY() / vehicleMass);
            wheel.getVehicle().recalculateVelocityByWheel(wheel);
        }
    }
}
