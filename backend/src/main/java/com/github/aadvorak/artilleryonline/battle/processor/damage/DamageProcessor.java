package com.github.aadvorak.artilleryonline.battle.processor.damage;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.statistics.StatisticsProcessor;
import com.github.aadvorak.artilleryonline.battle.updates.RoomStateUpdate;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class DamageProcessor {

    public static void processHitVehicle(VehicleCalculations vehicle, MissileCalculations missile,
                                         Collision collision, BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(vehicle.getModel().getUserId(), missile.getModel().getUserId(), battle.getModel());
        processHit(missile, collision, battle);
        var headHit = Hit.of(collision, missile);
        applyDamageToVehicle(headHit.damage(), vehicle.getModel(),
                battle.getModel(), missile.getModel().getUserId());
    }

    public static void processHitVehicle(VehicleCalculations vehicle, ShellCalculations shell,
                                         Collision collision, BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(vehicle.getModel().getUserId(), shell.getModel().getUserId(), battle.getModel());
        var shellSpecs = shell.getModel().getSpecs();
        var hit = Hit.of(collision, shell);
        if (shellSpecs.getType().isAP()) {
            var damage = getAPDamage(hit, vehicle.getModel());
            applyDamageToVehicle(damage, vehicle.getModel(), battle.getModel(),
                    shell.getModel().getUserId());
        }
        if (shellSpecs.getType().isHE()) {
            processHEDamage(hit, battle);
        }
    }

    public static void processHitDrone(DroneCalculations drone, ShellCalculations shell,
                                       Collision collision, BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(drone.getModel().getUserId(), shell.getModel().getUserId(), battle.getModel());
        var shellSpecs = shell.getModel().getSpecs();
        var hit = Hit.of(collision, shell);
        if (shellSpecs.getType().isHE()) {
            processHEDamage(hit, battle);
        }
    }

    public static void processHitTrack(VehicleCalculations vehicle, MissileCalculations missile,
                                       Collision collision, BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(vehicle.getModel().getUserId(), missile.getModel().getUserId(), battle.getModel());
        processTrackBreak(missile.getModel().getSpecs().getCaliber(), missile.getModel().getUserId(),
                vehicle.getModel(), battle.getModel());
        processHit(missile, collision, battle);
    }

    public static void processHitTrack(VehicleCalculations vehicle, ShellCalculations shell,
                                       Collision collision, BattleCalculations battle) {
        StatisticsProcessor.increaseDirectHits(vehicle.getModel().getUserId(), shell.getModel().getUserId(), battle.getModel());
        var shellSpecs = shell.getModel().getSpecs();
        processTrackBreak(shellSpecs.getCaliber(), shell.getModel().getUserId(),
                vehicle.getModel(), battle.getModel());
        var hit = Hit.of(collision, shell);
        if (shellSpecs.getType().isHE()) {
            processHEDamage(hit, battle);
        }
    }

    public static void processHit(ShellCalculations shell, Collision collision, BattleCalculations battle) {
        var shellSpecs = shell.getModel().getSpecs();
        var hit = Hit.of(collision, shell);
        if (shellSpecs.getType().isHE()) {
            processHEDamage(hit, battle);
        }
    }

    public static void processHit(MissileCalculations missile, Collision collision, BattleCalculations battle) {
        var hit = Hit.of(collision, missile);
        processHEDamage(hit, battle);
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

    private static void processTrackBreak(double caliber, Long userId, VehicleModel vehicleModel, BattleModel battleModel) {
        if (caliber >= vehicleModel.getSpecs().getMinTrackHitCaliber()) {
            StatisticsProcessor.increaseTrackBreaks(vehicleModel.getUserId(), userId, battleModel);
            var trackState = vehicleModel.getState().getTrackState();
            trackState.setBroken(true);
            trackState.setRepairRemainTime(vehicleModel.getSpecs().getTrackRepairTime());
            vehicleModel.getUpdate().setUpdated();
        }
    }

    private static void processHEDamage(Hit hit, BattleCalculations battle) {
        if (hit.collision().getPair().second() instanceof VehicleCalculations vehicle
                && hit.collision().getPair().first() instanceof ShellCalculations shell) {
            if (isPenetrated(hit, vehicle.getModel())) {
                var damageCoefficient = ShellType.BMB.equals(shell.getModel().getSpecs().getType())
                        ? 1.0 : BattleUtils.generateRandom(1.2, 1.8);
                applyDamageToVehicle(hit.damage() * damageCoefficient,
                        vehicle.getModel(), battle.getModel(), hit.userId());
                return;
            }
        }
        processGroundDamage(hit, battle.getModel());
        battle.getMissiles().forEach(missile -> {
            var distanceToTarget = hit.position().distanceTo(missile.getPosition());
            if (distanceToTarget < hit.radius()) {
                battle.getModel().getUpdates().removeMissile(missile.getId());
            }
        });
        battle.getDrones().forEach(drone -> {
            var distanceToTarget = hit.position().distanceTo(drone.getPosition())
                    - drone.getModel().getSpecs().getHullRadius();
            if (distanceToTarget < hit.radius()) {
                drone.getModel().getState().setDestroyed(true);
            }
        });
        battle.getVehicles().forEach(vehicle -> {
            var distanceToTarget = hit.position().distanceTo(vehicle.getPosition())
                    - vehicle.getModel().getPreCalc().getMaxRadius();
            if (distanceToTarget <= 0) {
                applyDamageToVehicle(0.5 * hit.damage(), vehicle.getModel(), battle.getModel(), hit.userId());
            } else if (distanceToTarget < hit.radius()) {
                StatisticsProcessor.increaseIndirectHits(vehicle.getModel().getUserId(), hit.userId(), battle.getModel());
                applyDamageToVehicle(0.5 * hit.damage() * (hit.radius() - distanceToTarget)
                        / hit.radius(), vehicle.getModel(), battle.getModel(), hit.userId());
            }
            processHETrackDamage(vehicle, hit, battle);
        });
    }

    private static void processHETrackDamage(VehicleCalculations vehicle, Hit hit,
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
            vehicle.getModel().getUpdate().setUpdated();
        }
    }

    private static boolean isWheelHitByHE(Position hitPosition, Position wheelPosition,
                                          double shellHitRadius, double wheelRadius) {
        return hitPosition.distanceTo(wheelPosition) <= 0.6 * shellHitRadius - wheelRadius;
    }

    private static void processGroundDamage(Hit hit, BattleModel battleModel) {
        var groundLine = battleModel.getRoom().getState().getGroundLine();
        if (groundLine == null) {
            return;
        }
        var damageRadius = hit.radius() * 0.6;
        var damageIndexes = BattleUtils.getGroundIndexesBetween(hit.position().getX() - damageRadius,
                hit.position().getX() + damageRadius, battleModel.getRoom());
        if (damageIndexes.isEmpty()) {
            return;
        }
        // apply damage
        for (var index : damageIndexes) {
            var groundPosition = BattleUtils.getGroundPosition(index, battleModel.getRoom());
            var explosionShiftY = getExplosionShiftY(groundPosition.getX(), damageRadius, hit.position());
            var minY = hit.position().getY() - explosionShiftY;
            var groundY = groundPosition.getY();
            var diffY = groundY - minY;
            if (diffY > explosionShiftY) {
                groundY -= explosionShiftY;
            } else if (diffY > 0) {
                groundY -= diffY;
            }
            groundY = groundY > 0 ? groundY : 0;
            groundLine.set(index, groundY);
        }
        // apply smooth
        var smoothSizeCoefficient = 1.3;
        var smoothIndexes = BattleUtils.getGroundIndexesBetween(
                hit.position().getX() - damageRadius * smoothSizeCoefficient,
                hit.position().getX() + damageRadius * smoothSizeCoefficient,
                battleModel.getRoom()
        );
        BattleUtils.gaussianFilter(groundLine, smoothIndexes);
        // create update
        var roomStateUpdate = new RoomStateUpdate().setBegin(smoothIndexes.get(0));
        for (var index : smoothIndexes) {
            roomStateUpdate.getGroundLinePart()
                    .add(groundLine.get(index));
        }
        battleModel.getUpdates().addRoomStateUpdate(roomStateUpdate);
    }

    private static double getExplosionShiftY(double x, double damageRadius, Position damagePosition) {
        var discriminant = 4.0 * (Math.pow(damageRadius, 2) - Math.pow(x - damagePosition.getX(), 2));
        if (discriminant <= 0) {
            return 0.0;
        } else {
            return Math.sqrt(discriminant) / 2;
        }
    }

    private static double getAPDamage(Hit hit, VehicleModel vehicleModel) {
        if (isPenetrated(hit, vehicleModel)) {
            return hit.damage();
        }
        return 0.0;
    }

    private static boolean isPenetrated(Hit hit, VehicleModel vehicleModel) {
        var first = hit.collision().getPair().first();
        if (first instanceof ShellCalculations shell) {
            var velocityRatio = hit.collision().getClosingVelocity() / shell.getModel().getSpecs().getVelocity();
            var shellPenetration = shell.getModel().getSpecs().getPenetration();
            var resultPenetration = velocityRatio * velocityRatio * shellPenetration;
            var vehicleArmor = vehicleModel.getSpecs().getArmor().get(hit.collision().getHitSurface());
            return resultPenetration >= vehicleArmor;
        }
        return false;
    }
}
