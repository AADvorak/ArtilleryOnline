package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class ShellDamageProcessor {

    public static void processHitVehicle(Position hitPosition, VehicleModel vehicleModel,
                                         ShellSpecs shellSpecs, BattleModel battleModel) {
        if (ShellType.AP.equals(shellSpecs.getType())) {
            applyDamageToVehicle(shellSpecs.getDamage(), vehicleModel, battleModel);
        } else if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
        }
    }

    public static void processHitTrack(Position hitPosition, VehicleModel vehicleModel,
                                         ShellSpecs shellSpecs, BattleModel battleModel) {
        var trackState = vehicleModel.getState().getTrackState();
        trackState.setBroken(true);
        trackState.setRepairRemainTime(vehicleModel.getSpecs().getTrackRepairTime());
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
        }
    }

    public static void processHitGround(Position hitPosition, ShellSpecs shellSpecs, BattleModel battleModel) {
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
        }
        processGroundDamage(hitPosition, shellSpecs, battleModel);
        // todo possible track destruction
    }

    private static void calculateHEDamage(Position hitPosition, ShellSpecs shellSpecs, BattleModel battleModel) {
        battleModel.getVehicles().values().forEach(vehicleModel -> {
            var distanceToTarget = hitPosition.distanceTo(vehicleModel.getState().getPosition())
                    - vehicleModel.getSpecs().getRadius();
            if (distanceToTarget <= 0) {
                applyDamageToVehicle(shellSpecs.getDamage(), vehicleModel, battleModel);
            } else if (distanceToTarget < shellSpecs.getRadius()) {
                applyDamageToVehicle(shellSpecs.getDamage() * (shellSpecs.getRadius() - distanceToTarget)
                        / shellSpecs.getRadius(), vehicleModel, battleModel);
            }
        });
    }

    private static void applyDamageToVehicle(double damage, VehicleModel vehicleModel, BattleModel battleModel) {
        var hitPoints = vehicleModel.getState().getHitPoints() - damage;
        if (hitPoints <= 0) {
            battleModel.removeVehicleById(vehicleModel.getId());
        } else {
            vehicleModel.getState().setHitPoints(hitPoints);
        }
    }

    /**
     * todo improve, smooth ground after explosion
     */
    private static void processGroundDamage(Position hitPosition, ShellSpecs shellSpecs, BattleModel battleModel) {
        var damageRadius = shellSpecs.getRadius();
        var groundIndexes = BattleUtils.getGroundIndexesBetween(hitPosition.getX() - damageRadius,
                hitPosition.getX() + damageRadius, battleModel.getRoom());
        for (var groundIndex : groundIndexes) {
            var groundPosition = BattleUtils.getGroundPosition(groundIndex, battleModel.getRoom());
            var explosionShiftY = getExplosionShiftY(groundPosition.getX(), hitPosition, damageRadius);
            var minY = hitPosition.getY() - explosionShiftY;
            var groundY = groundPosition.getY();
            var diffY = groundY - minY;
            if (diffY > explosionShiftY) {
                groundY -= explosionShiftY;
            } else if (diffY > 0) {
                groundY -= diffY;
            }
            battleModel.getRoom().getState().getGroundLine().set(groundIndex, groundY > 0 ? groundY : 0);
        }
    }

    private static double getExplosionShiftY(double x, Position hitPosition, double damageRadius) {
        var discriminant = 4 * (Math.pow(damageRadius, 2) - Math.pow(x - hitPosition.getX(), 2));
        if (discriminant <= 0) {
            return 0;
        } else {
            return Math.sqrt(discriminant) / 2;
        }
    }
}
