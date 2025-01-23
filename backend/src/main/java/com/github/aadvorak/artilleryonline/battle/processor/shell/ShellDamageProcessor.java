package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.statistics.StatisticsProcessor;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import com.github.aadvorak.artilleryonline.battle.updates.RoomStateUpdate;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class ShellDamageProcessor {

    public static void processHitVehicle(Position hitPosition, VehicleModel vehicleModel,
                                         ShellModel shellModel, BattleModel battleModel) {
        StatisticsProcessor.increaseDirectHits(vehicleModel, shellModel, battleModel);
        var shellSpecs = shellModel.getSpecs();
        if (ShellType.AP.equals(shellSpecs.getType())) {
            applyDamageToVehicle(shellSpecs.getDamage(), vehicleModel, shellModel, battleModel);
        } else if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellModel, battleModel);
            processGroundDamage(hitPosition, shellSpecs, battleModel);
        }
    }

    public static void processHitTrack(Position hitPosition, VehicleModel vehicleModel,
                                       ShellModel shellModel, BattleModel battleModel) {
        StatisticsProcessor.increaseDirectHits(vehicleModel, shellModel, battleModel);
        var trackState = vehicleModel.getState().getTrackState();
        var shellSpecs = shellModel.getSpecs();
        if (shellSpecs.getCaliber() >= vehicleModel.getSpecs().getMinTrackHitCaliber()) {
            StatisticsProcessor.increaseTrackBreaks(vehicleModel, shellModel, battleModel);
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicleModel.getSpecs().getTrackRepairTime());
            vehicleModel.setUpdated(true);
        }
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellModel, battleModel);
            processGroundDamage(hitPosition, shellSpecs, battleModel);
        }
    }

    public static void processHitGround(Position hitPosition, ShellModel shellModel, BattleModel battleModel) {
        var shellSpecs = shellModel.getSpecs();
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellModel, battleModel);
        }
        processGroundDamage(hitPosition, shellSpecs, battleModel);
    }

    private static void calculateHEDamage(Position hitPosition, ShellModel shellModel, BattleModel battleModel) {
        var shellSpecs = shellModel.getSpecs();
        battleModel.getVehicles().values().forEach(vehicleModel -> {
            var distanceToTarget = hitPosition.distanceTo(vehicleModel.getState().getPosition())
                    - vehicleModel.getSpecs().getRadius();
            if (distanceToTarget <= 0) {
                applyDamageToVehicle(shellSpecs.getDamage(), vehicleModel, shellModel, battleModel);
            } else if (distanceToTarget < shellSpecs.getRadius()) {
                StatisticsProcessor.increaseIndirectHits(vehicleModel, shellModel, battleModel);
                applyDamageToVehicle(shellSpecs.getDamage() * (shellSpecs.getRadius() - distanceToTarget)
                        / shellSpecs.getRadius(), vehicleModel, shellModel, battleModel);
            }
            calculateHETrackDamage(hitPosition, vehicleModel, shellModel, battleModel);
        });
    }

    private static void applyDamageToVehicle(double damage, VehicleModel vehicleModel,
                                             ShellModel shellModel, BattleModel battleModel) {
        StatisticsProcessor.increaseDamage(Math.min(damage, vehicleModel.getState().getHitPoints()),
                vehicleModel.getUserId(), shellModel.getUserId(), battleModel);
        var hitPoints = vehicleModel.getState().getHitPoints() - damage;
        if (hitPoints <= 0) {
            vehicleModel.getState().setHitPoints(0.0);
            battleModel.getUpdates().removeVehicle(battleModel.getVehicleKeyById(vehicleModel.getId()));
            if (shellModel.getUserId() != null) {
                battleModel.getStatistics().get(shellModel.getUserId()).increaseDestroyedVehicles();
            }
        } else {
            vehicleModel.getState().setHitPoints(hitPoints);
        }
    }

    private static void calculateHETrackDamage(Position hitPosition, VehicleModel vehicleModel,
                                               ShellModel shellModel, BattleModel battleModel) {
        var trackState = vehicleModel.getState().getTrackState();
        if (trackState.isBroken()
                || vehicleModel.getSpecs().getName().equals(VehicleSpecsPreset.HEAVY.getSpecs().getName())) {
            return;
        }
        var shellHitRadius = shellModel.getSpecs().getRadius();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel);
        var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel);
        if (isWheelHitByHE(hitPosition, rightWheelPosition, shellHitRadius, wheelRadius)
                || isWheelHitByHE(hitPosition, leftWheelPosition, shellHitRadius, wheelRadius)) {
            StatisticsProcessor.increaseTrackBreaks(vehicleModel, shellModel, battleModel);
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicleModel.getSpecs().getTrackRepairTime());
            vehicleModel.setUpdated(true);
        }
    }

    private static boolean isWheelHitByHE(Position hitPosition, Position wheelPosition,
                                          double shellHitRadius, double wheelRadius) {
        return hitPosition.distanceTo(wheelPosition) <= 0.6 * shellHitRadius - wheelRadius;
    }

    private static void processGroundDamage(Position hitPosition, ShellSpecs shellSpecs, BattleModel battleModel) {
        var damageRadius = shellSpecs.getRadius();
        var damageIndexes = BattleUtils.getGroundIndexesBetween(hitPosition.getX() - damageRadius,
                hitPosition.getX() + damageRadius, battleModel.getRoom());
        if (damageIndexes.isEmpty()) {
            return;
        }
        // apply damage
        for (var index : damageIndexes) {
            var groundPosition = BattleUtils.getGroundPosition(index, battleModel.getRoom());
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
            battleModel.getRoom().getState().getGroundLine().set(index, groundY);
        }
        // apply smooth
        var smoothSizeCoefficient = 1.3;
        var smoothIndexes = BattleUtils.getGroundIndexesBetween(
                hitPosition.getX() - damageRadius * smoothSizeCoefficient,
                hitPosition.getX() + damageRadius * smoothSizeCoefficient,
                battleModel.getRoom()
        );
        BattleUtils.gaussianFilter(battleModel.getRoom().getState().getGroundLine(), smoothIndexes);
        // create update
        var roomStateUpdate = new RoomStateUpdate().setBegin(smoothIndexes.get(0));
        for (var index : smoothIndexes) {
            roomStateUpdate.getGroundLinePart()
                    .add(battleModel.getRoom().getState().getGroundLine().get(index));
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
}
