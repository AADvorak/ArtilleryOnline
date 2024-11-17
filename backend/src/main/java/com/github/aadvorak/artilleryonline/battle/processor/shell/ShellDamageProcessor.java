package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import com.github.aadvorak.artilleryonline.battle.updates.RoomStateUpdate;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class ShellDamageProcessor {

    public static void processHitVehicle(Position hitPosition, VehicleModel vehicleModel,
                                         ShellModel shellModel, BattleModel battleModel) {
        var shellSpecs = shellModel.getSpecs();
        if (ShellType.AP.equals(shellSpecs.getType())) {
            applyDamageToVehicle(shellSpecs.getDamage(), vehicleModel, battleModel);
        } else if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
            processGroundDamage(hitPosition, shellSpecs, battleModel);
        }
        pushHitVehicle(vehicleModel, shellModel);
    }

    public static void processHitTrack(Position hitPosition, VehicleModel vehicleModel,
                                         ShellSpecs shellSpecs, BattleModel battleModel) {
        var trackState = vehicleModel.getState().getTrackState();
        if (shellSpecs.getCaliber() >= vehicleModel.getSpecs().getMinTrackHitCaliber()) {
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicleModel.getSpecs().getTrackRepairTime());
            vehicleModel.setUpdated(true);
        }
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
            processGroundDamage(hitPosition, shellSpecs, battleModel);
        }
    }

    public static void processHitGround(Position hitPosition, ShellSpecs shellSpecs, BattleModel battleModel) {
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
        }
        processGroundDamage(hitPosition, shellSpecs, battleModel);
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
            calculateHETrackDamage(hitPosition, vehicleModel, shellSpecs);
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

    private static void calculateHETrackDamage(Position hitPosition, VehicleModel vehicleModel, ShellSpecs shellSpecs) {
        var trackState = vehicleModel.getState().getTrackState();
        if (trackState.isBroken()
                || vehicleModel.getSpecs().getName().equals(VehicleSpecsPreset.HEAVY.getSpecs().getName())) {
            return;
        }
        var shellHitRadius = shellSpecs.getRadius();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel);
        var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel);
        if (isWheelHitByHE(hitPosition, rightWheelPosition, shellHitRadius, wheelRadius)
                || isWheelHitByHE(hitPosition, leftWheelPosition, shellHitRadius, wheelRadius)) {
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicleModel.getSpecs().getTrackRepairTime());
            vehicleModel.setUpdated(true);
        }
    }

    private static boolean isWheelHitByHE(Position hitPosition, Position wheelPosition,
                                          double shellHitRadius, double wheelRadius) {
        return hitPosition.distanceTo(wheelPosition) <= 0.6 * shellHitRadius - wheelRadius;
    }

    /**
     * todo improve, smooth ground after explosion
     */
    private static void processGroundDamage(Position hitPosition, ShellSpecs shellSpecs, BattleModel battleModel) {
        var damageRadius = shellSpecs.getRadius();
        var groundIndexes = BattleUtils.getGroundIndexesBetween(hitPosition.getX() - damageRadius,
                hitPosition.getX() + damageRadius, battleModel.getRoom());
        if (groundIndexes.isEmpty()) {
            return;
        }
        var roomStateUpdate = new RoomStateUpdate().setBegin(groundIndexes.get(0));
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
            groundY = groundY > 0 ? groundY : 0;
            battleModel.getRoom().getState().getGroundLine().set(groundIndex, groundY);
            roomStateUpdate.getGroundLinePart().add(groundY);
        }
        battleModel.getUpdates().addRoomStateUpdate(roomStateUpdate);
    }

    private static double getExplosionShiftY(double x, Position hitPosition, double damageRadius) {
        var discriminant = 4.0 * (Math.pow(damageRadius, 2) - Math.pow(x - hitPosition.getX(), 2));
        if (discriminant <= 0) {
            return 0.0;
        } else {
            return Math.sqrt(discriminant) / 2;
        }
    }

    private static void pushHitVehicle(VehicleModel vehicleModel, ShellModel shellModel) {
        var vehicleVelocity = vehicleModel.getState().getVelocity();
        var pushCoefficient = 0.1 * shellModel.getSpecs().getPushCoefficient() / vehicleModel.getPreCalc().getMass();
        if (ShellType.HE.equals(shellModel.getSpecs().getType())) {
            pushCoefficient /= 2.0;
        }
        var shellVelocity = shellModel.getState().getVelocity();
        var shellAngle = shellModel.getState().getAngle();
        vehicleVelocity
                .setX(vehicleVelocity.getX() + pushCoefficient * shellVelocity * Math.cos(shellAngle))
                .setY(vehicleVelocity.getY() + pushCoefficient * shellVelocity * Math.sin(shellAngle));
        vehicleModel.setUpdated(true);
    }
}
