package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.processor.shell.ShellDamageProcessor;

public class ShellVehicleCollisionsProcessor {

    public static void process(ShellCalculations shell, BattleCalculations battle) {
        var collision = ShellVehicleCollisionsDetector.detectFirst(shell, battle);
        if (collision != null) {
            shell.getCollisions().add(collision);
            var hitObject = collision.getPair().second();
            if (hitObject instanceof VehicleCalculations vehicle) {
                ShellDamageProcessor.processHitVehicle(shell.getNext().getPosition(), vehicle.getModel(),
                        shell.getModel(), battle.getModel());
            }
            if (hitObject instanceof WheelCalculations wheel) {
                ShellDamageProcessor.processHitTrack(shell.getNext().getPosition(), wheel.getModel(),
                        shell.getModel(), battle.getModel());
            }
            pushVehicle(collision);
        }
    }

    private static void pushVehicle(Collision collision) {
        var shellType = ((ShellCalculations) collision.getPair().first()).getModel().getSpecs().getType();
        var shellMass = (ShellType.HE.equals(shellType) ? 0.5 : 1.0) * collision.getPair().first().getMass();
        var vehicleMass = collision.getPair().second().getMass();
        if (collision.getPair().second() instanceof VehicleCalculations vehicle) {
            // todo linear velocity
            /*var vehicleVelocity = vehicle.getModel().getState().getVelocity();
            var shellVelocity = collision.getPair().first().getVelocity();
            vehicleVelocity.setX(vehicleVelocity.getX() + shellMass * shellVelocity.getX() / vehicleMass);
            vehicleVelocity.setY(vehicleVelocity.getY() + shellMass * shellVelocity.getY() / vehicleMass);*/
            // todo normal velocity
            var vehicleVelocitiesProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().second());
            var shellVelocitiesProjections = collision.getVelocitiesProjections().first();
            vehicleVelocitiesProjections.setNormal(vehicleVelocitiesProjections.getNormal()
                    + shellMass * shellVelocitiesProjections.getNormal() / vehicleMass);
            vehicle.setVelocity(vehicleVelocitiesProjections.recoverVelocity());
            // todo angle velocity
            var vehicleVelocity = vehicle.getModel().getState().getVelocity();
            var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
            vehicleVelocity.setAngle(vehicleVelocity.getAngle()
                    + shellVelocitiesProjections.getTangential() * (shellMass / vehicleMass) / vehicleRadius);
        }
        if (collision.getPair().second() instanceof WheelCalculations wheel) {
            var wheelVelocity = wheel.getVelocity();
            var shellVelocity = collision.getPair().first().getVelocity();
            wheelVelocity.setX(wheelVelocity.getX() + shellMass * shellVelocity.getX() / vehicleMass);
            wheelVelocity.setY(wheelVelocity.getY() + shellMass * shellVelocity.getY() / vehicleMass);
            wheel.getVehicle().recalculateVelocityByWheel(wheel);
        }
    }
}
