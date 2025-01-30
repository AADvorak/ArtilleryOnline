package com.github.aadvorak.artilleryonline.battle.processor.damage;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.statistics.StatisticsProcessor;
import com.github.aadvorak.artilleryonline.battle.updates.RoomStateUpdate;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class DamageProcessor {

    public static void processHitVehicle(VehicleCalculations vehicle, ShellCalculations shell,
                                         BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(vehicle.getModel().getUserId(), shell.getModel().getUserId(), battle.getModel());
        var shellSpecs = shell.getModel().getSpecs();
        var hit = Hit.of(shell);
        if (ShellType.AP.equals(shellSpecs.getType())) {
            applyDamageToVehicle(shellSpecs.getDamage(), vehicle.getModel(), battle.getModel(),
                    shell.getModel().getUserId());
        } else if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hit, battle);
            processGroundDamage(hit, battle.getModel());
        }
    }

    public static void processHitTrack(VehicleCalculations vehicle, ShellCalculations shell,
                                       BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(vehicle.getModel().getUserId(), shell.getModel().getUserId(), battle.getModel());
        var trackState = vehicle.getModel().getState().getTrackState();
        var shellSpecs = shell.getModel().getSpecs();
        var hit = Hit.of(shell);
        if (shellSpecs.getCaliber() >= vehicle.getModel().getSpecs().getMinTrackHitCaliber()) {
            StatisticsProcessor.increaseTrackBreaks(vehicle.getModel().getUserId(), shell.getModel().getUserId(), battle.getModel());
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicle.getModel().getSpecs().getTrackRepairTime());
            vehicle.getModel().setUpdated(true);
        }
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hit, battle);
            processGroundDamage(hit, battle.getModel());
        }
    }

    public static void processHitGround(ShellCalculations shell, BattleCalculations battle) {
        var shellSpecs = shell.getModel().getSpecs();
        var hit = Hit.of(shell);
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hit, battle);
        }
        processGroundDamage(hit, battle.getModel());
    }

    public static void processHitGround(MissileCalculations missile, BattleCalculations battle) {
        var hit = Hit.of(missile);
        calculateHEDamage(hit, battle);
        processGroundDamage(hit, battle.getModel());
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

    private static void calculateHEDamage(Hit hit, BattleCalculations battle) {
        battle.getVehicles().forEach(vehicle -> {
            var distanceToTarget = hit.position().distanceTo(vehicle.getPosition())
                    - vehicle.getModel().getSpecs().getRadius();
            if (distanceToTarget <= 0) {
                applyDamageToVehicle(hit.damage(), vehicle.getModel(), battle.getModel(), hit.userId());
            } else if (distanceToTarget < hit.radius()) {
                StatisticsProcessor.increaseIndirectHits(vehicle.getModel().getUserId(), hit.userId(), battle.getModel());
                applyDamageToVehicle(hit.damage() * (hit.radius() - distanceToTarget)
                        / hit.radius(), vehicle.getModel(), battle.getModel(), hit.userId());
            }
            calculateHETrackDamage(vehicle, hit, battle);
        });
    }

    private static void calculateHETrackDamage(VehicleCalculations vehicle, Hit hit,
                                               BattleCalculations battle) {
        var trackState = vehicle.getModel().getState().getTrackState();
        if (trackState.isBroken()
                || vehicle.getModel().getSpecs().getName().equals(VehicleSpecsPreset.HEAVY.getSpecs().getName())) {
            return;
        }
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var rightWheelPosition = vehicle.getRightWheel().getPosition();
        var leftWheelPosition = vehicle.getLeftWheel().getPosition();
        if (isWheelHitByHE(hit.position(), rightWheelPosition, hit.radius(), wheelRadius)
                || isWheelHitByHE(hit.position(), leftWheelPosition, hit.radius(), wheelRadius)) {
            StatisticsProcessor.increaseTrackBreaks(vehicle.getModel().getUserId(), hit.userId(), battle.getModel());
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicle.getModel().getSpecs().getTrackRepairTime());
            vehicle.getModel().setUpdated(true);
        }
    }

    private static boolean isWheelHitByHE(Position hitPosition, Position wheelPosition,
                                          double shellHitRadius, double wheelRadius) {
        return hitPosition.distanceTo(wheelPosition) <= 0.6 * shellHitRadius - wheelRadius;
    }

    private static void processGroundDamage(Hit hit, BattleModel battleModel) {
        var damageIndexes = BattleUtils.getGroundIndexesBetween(hit.position().getX() - hit.radius(),
                hit.position().getX() + hit.radius(), battleModel.getRoom());
        if (damageIndexes.isEmpty()) {
            return;
        }
        // apply damage
        for (var index : damageIndexes) {
            var groundPosition = BattleUtils.getGroundPosition(index, battleModel.getRoom());
            var explosionShiftY = getExplosionShiftY(groundPosition.getX(), hit);
            var minY = hit.position().getY() - explosionShiftY;
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
                hit.position().getX() - hit.radius() * smoothSizeCoefficient,
                hit.position().getX() + hit.radius() * smoothSizeCoefficient,
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

    private static double getExplosionShiftY(double x, Hit hit) {
        var discriminant = 4.0 * (Math.pow(hit.radius(), 2) - Math.pow(x - hit.position().getX(), 2));
        if (discriminant <= 0) {
            return 0.0;
        } else {
            return Math.sqrt(discriminant) / 2;
        }
    }
}
