package com.github.aadvorak.artilleryonline.battle.processor.damage;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.statistics.StatisticsProcessor;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import com.github.aadvorak.artilleryonline.battle.updates.RoomStateUpdate;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class DamageProcessor {

    public static void processHitVehicle(VehicleCalculations vehicle, ShellCalculations shell,
                                         BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(vehicle.getModel(), shell.getModel(), battle.getModel());
        var shellSpecs = shell.getModel().getSpecs();
        if (ShellType.AP.equals(shellSpecs.getType())) {
            applyDamageToVehicle(shellSpecs.getDamage(), vehicle.getModel(), battle.getModel(),
                    shell.getModel().getUserId());
        } else if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(shell, battle);
            processGroundDamage(shell.getNext().getPosition(), shellSpecs, battle.getModel());
        }
    }

    public static void processHitTrack(VehicleCalculations vehicle, ShellCalculations shell,
                                       BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(vehicle.getModel(), shell.getModel(), battle.getModel());
        var trackState = vehicle.getModel().getState().getTrackState();
        var shellSpecs = shell.getModel().getSpecs();
        if (shellSpecs.getCaliber() >= vehicle.getModel().getSpecs().getMinTrackHitCaliber()) {
            StatisticsProcessor.increaseTrackBreaks(vehicle.getModel(), shell.getModel(), battle.getModel());
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicle.getModel().getSpecs().getTrackRepairTime());
            vehicle.getModel().setUpdated(true);
        }
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(shell, battle);
            processGroundDamage(shell.getNext().getPosition(), shellSpecs, battle.getModel());
        }
    }

    public static void processHitGround(ShellCalculations shell, BattleCalculations battle) {
        var shellSpecs = shell.getModel().getSpecs();
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(shell, battle);
        }
        processGroundDamage(shell.getNext().getPosition(), shellSpecs, battle.getModel());
    }

    public static void applyDamageToVehicle(double damage, VehicleModel vehicleModel, BattleModel battleModel, Long userId) {
        StatisticsProcessor.increaseDamage(Math.min(damage, vehicleModel.getState().getHitPoints()),
                vehicleModel.getUserId(), userId, battleModel);
        var hitPoints = vehicleModel.getState().getHitPoints() - damage;
        if (hitPoints <= 0) {
            vehicleModel.getState().setHitPoints(0.0);
            battleModel.getUpdates().removeVehicle(battleModel.getVehicleKeyById(vehicleModel.getId()));
            if (userId != null) {
                battleModel.getStatistics().get(userId).increaseDestroyedVehicles();
            }
        } else {
            vehicleModel.getState().setHitPoints(hitPoints);
        }
    }

    private static void calculateHEDamage(ShellCalculations shell, BattleCalculations battle) {
        var shellSpecs = shell.getModel().getSpecs();
        var hitPosition = shell.getNext().getPosition();
        battle.getVehicles().forEach(vehicle -> {
            var distanceToTarget = hitPosition.distanceTo(vehicle.getPosition())
                    - vehicle.getModel().getSpecs().getRadius();
            if (distanceToTarget <= 0) {
                applyDamageToVehicle(shellSpecs.getDamage(), vehicle.getModel(), battle.getModel(),
                        shell.getModel().getUserId());
            } else if (distanceToTarget < shellSpecs.getRadius()) {
                StatisticsProcessor.increaseIndirectHits(vehicle.getModel(), shell.getModel(), battle.getModel());
                applyDamageToVehicle(shellSpecs.getDamage() * (shellSpecs.getRadius() - distanceToTarget)
                        / shellSpecs.getRadius(), vehicle.getModel(), battle.getModel(), shell.getModel().getUserId());
            }
            calculateHETrackDamage(vehicle, shell, battle);
        });
    }

    private static void calculateHETrackDamage(VehicleCalculations vehicle, ShellCalculations shell,
                                               BattleCalculations battle) {
        var trackState = vehicle.getModel().getState().getTrackState();
        if (trackState.isBroken()
                || vehicle.getModel().getSpecs().getName().equals(VehicleSpecsPreset.HEAVY.getSpecs().getName())) {
            return;
        }
        var shellHitRadius = shell.getModel().getSpecs().getRadius();
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var rightWheelPosition = vehicle.getRightWheel().getPosition();
        var leftWheelPosition = vehicle.getLeftWheel().getPosition();
        var hitPosition = shell.getNext().getPosition();
        if (isWheelHitByHE(hitPosition, rightWheelPosition, shellHitRadius, wheelRadius)
                || isWheelHitByHE(hitPosition, leftWheelPosition, shellHitRadius, wheelRadius)) {
            StatisticsProcessor.increaseTrackBreaks(vehicle.getModel(), shell.getModel(), battle.getModel());
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicle.getModel().getSpecs().getTrackRepairTime());
            vehicle.getModel().setUpdated(true);
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
