package com.github.aadvorak.artilleryonline.battle.processor.bot;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.config.AmmoConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.ShellSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleLaunchDroneProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleLaunchMissileProcessor;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class BotsProcessor {

    private static final long DELAY = 200;

    private final TargetDataCalculator targetDataCalculator =  new TargetDataCalculator();

    public void process(BattleCalculations battle) {
        if (battle.getTime() - battle.getBotsData().getLastTimeProcessed() > DELAY) {
            battle.getVehicles().stream()
                    .filter(vehicle -> battle.getBotsData().getVehicleIds().contains(vehicle.getId()))
                    .forEach(vehicle -> processVehicle(vehicle, battle));
            battle.getBotsData().setLastTimeProcessed(battle.getTime());
        }
    }

    private void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var state = vehicle.getModel().getState();
        var oldTriggerPushed = state.getGunState().isTriggerPushed();
        var oldGunRotatingDirection = state.getGunState().getRotatingDirection();
        var oldJetActive = state.getJetState().isActive();
        var oldMovingDirection = state.getMovingDirection();
        var vehicleX = vehicle.getPosition().getX();
        var isMovingToBox = false;
        var otherVehiclePositions = battle.getVehicles().stream()
                .filter(item -> !vehicle.getId().equals(item.getId()))
                .map(VehicleCalculations::getPosition)
                .collect(Collectors.toSet());
        var enemyVehiclePositions = battle.getVehicles().stream()
                .filter(item -> battle.allowedTarget(vehicle.getId(), item.getId()))
                .collect(Collectors.toMap(VehicleCalculations::getPosition, VehicleCalculations::getId));
        var roomSpecs = battle.getModel().getRoom().getSpecs();
        var vehicleMaxRadius = vehicle.getModel().getPreCalc().getMaxRadius();
        var moveRightBlocked = roomSpecs.getRightTop().getX() - vehicleX < vehicleMaxRadius;
        var moveLeftBlocked = vehicleX - roomSpecs.getLeftBottom().getX() < vehicleMaxRadius;
        state.setMovingDirection(null);
        var needHp = vehicle.getModel().getRelativeHp() < 1.0;
        var criticalNeedHp = vehicle.getModel().getRelativeHp() < 0.5;
        var needAmmo = vehicle.getModel().getRelativeAmmo() < 1.0;
        var criticalNeedAmmo = vehicle.getModel().getRelativeAmmo() < 0.3;
        var trackBroken = vehicle.getModel().getState().getTrackState().isBroken();
        var verticalJet = vehicle.getModel().getConfig().getJet().getType().equals(JetType.VERTICAL);
        if (criticalNeedHp || needHp && !trackBroken) {
            isMovingToBox = setMovingToBoxIfAvailable(battle.getBoxes(), BoxType.HP, state,
                    verticalJet ? null : otherVehiclePositions);
        }
        if (!isMovingToBox && (criticalNeedAmmo || needAmmo && !trackBroken)) {
            isMovingToBox = setMovingToBoxIfAvailable(battle.getBoxes(), BoxType.AMMO, state,
                    verticalJet ? null : otherVehiclePositions);
        }
        var closestEnemyPosition = GeometryUtils.findClosestPosition(vehicle.getPosition(), enemyVehiclePositions.keySet());
        if (closestEnemyPosition != null) {
            var closestEnemyId = enemyVehiclePositions.get(closestEnemyPosition);
            var closeBattleDistance = vehicleMaxRadius * 8;
            var closestEnemyDistance = vehicleX - closestEnemyPosition.getX();
            var isCloseBattle = Math.abs(closestEnemyDistance) < closeBattleDistance
                    && BattleUtils.getFirstPointUnderGround(new Segment(vehicle.getPosition(),
                    closestEnemyPosition), battle.getModel().getRoom()) == null;
            switchShellIfNeeded(vehicle.getId(), state, battle.getShells());
            launchDroneIfAvailable(vehicle.getModel(), battle.getModel());
            launchMissileConditional(vehicle.getModel(), battle.getModel(), isCloseBattle);
            var targetData = targetDataCalculator.calculate(vehicle, battle);
            state.getGunState().setTriggerPushed(targetData != null && targetData.vehicleId() != null
                    && battle.allowedTarget(vehicle.getId(), targetData.vehicleId()));
            if (isCloseBattle) {
                closeBattleTargeting(vehicle, targetData, closestEnemyPosition, closestEnemyId);
            } else {
                distantBattleTargeting(state, targetData, closestEnemyPosition, !isMovingToBox && !trackBroken);
            }
        }
        if (state.getMovingDirection() == null) {
            runFromShellIfExists(vehicle.getModel(), battle.getShells(), moveRightBlocked, moveLeftBlocked);
        }
        if (state.getMovingDirection() == null) {
            goAwayFromCloseVehicle(vehicle, otherVehiclePositions, moveRightBlocked, moveLeftBlocked);
        }
        if (state.getMovingDirection() == null) {
            var vehicleAngle = state.getPosition().getAngle();
            if (vehicleAngle > Math.PI / 4) {
                state.setMovingDirection(moveLeftBlocked ? MovingDirection.RIGHT : MovingDirection.LEFT);
            }
            if (vehicleAngle < -Math.PI / 4) {
                state.setMovingDirection(moveRightBlocked ? MovingDirection.LEFT : MovingDirection.RIGHT);
            }
        }
        controlJet(vehicle, battle);
        if (
                oldTriggerPushed != state.getGunState().isTriggerPushed()
                || oldJetActive != state.getJetState().isActive()
                || isChanged(oldGunRotatingDirection, state.getGunState().getRotatingDirection())
                || isChanged(oldMovingDirection, state.getMovingDirection())
        ) {
            vehicle.getModel().getUpdate().setUpdated();
        }
    }

    private boolean isChanged(Object oldValue, Object newValue) {
        return oldValue != null && newValue == null
                || oldValue == null && newValue != null
                || oldValue != null && !oldValue.equals(newValue);
    }

    private boolean setMovingToBoxIfAvailable(Set<BoxCalculations> boxes, BoxType boxType,
                                              VehicleState state, Set<Position> otherVehiclePositions) {
        var vehicleX = state.getPosition().getX();
        var boxesPositions = boxes.stream()
                .filter(box -> boxType.equals(box.getModel().getSpecs().getType()))
                .map(BoxCalculations::getPosition)
                .filter(position -> otherVehiclePositions == null
                        || notSeparatedByOtherVehicle(vehicleX, position.getX(), otherVehiclePositions))
                .collect(Collectors.toSet());
        var closestPosition = GeometryUtils.findClosestPosition(state.getPosition().getCenter(), boxesPositions);
        if (closestPosition != null) {
            var xDiff = closestPosition.getX() - vehicleX;
            if (Math.abs(xDiff) > 0.1) {
                state.setMovingDirection(xDiff > 0 ? MovingDirection.RIGHT :  MovingDirection.LEFT);
                return true;
            }
        }
        return false;
    }

    private void closeBattleTargeting(VehicleCalculations vehicle, TargetData  targetData,
                                      Position closestVehiclePosition, Integer closestVehicleId) {
        var state = vehicle.getModel().getState();
        var gunAngle = state.getPosition().getAngle() + state.getGunState().getAngle();
        var vehicleIsRight = closestVehiclePosition.getX() > vehicle.getPosition().getX();
        if (targetData == null || gunAngle > Math.PI / 4 && gunAngle < 3 * Math.PI / 4) {
            state.getGunState().setRotatingDirection(vehicleIsRight
                    ? MovingDirection.RIGHT : MovingDirection.LEFT);
        } else {
            var gunUp = false;
            if (targetData.vehicleId() == null || !targetData.vehicleId().equals(closestVehicleId)) {
                gunUp = vehicle.getPosition().distanceTo(closestVehiclePosition)
                        > vehicle.getPosition().distanceTo(targetData.contact().position());
            } else {
                gunUp = closestVehiclePosition.getY() > targetData.contact().position().getY();
            }
            if (vehicleIsRight) {
                state.getGunState().setRotatingDirection(gunUp ?  MovingDirection.LEFT : MovingDirection.RIGHT);
            } else {
                state.getGunState().setRotatingDirection(gunUp ?  MovingDirection.RIGHT : MovingDirection.LEFT);
            }
        }
    }

    private void distantBattleTargeting(VehicleState state, TargetData targetData,
                                        Position closestVehiclePosition, boolean allowMove) {
        var gunAngle = state.getPosition().getAngle() + state.getGunState().getAngle();
        if (gunAngle < Math.PI / 4) {
            state.getGunState().setRotatingDirection(MovingDirection.LEFT);
        } else if (gunAngle > 3 * Math.PI / 4) {
            state.getGunState().setRotatingDirection(MovingDirection.RIGHT);
        } else if (targetData == null) {
            state.getGunState().setRotatingDirection(gunAngle > Math.PI / 2 ? MovingDirection.RIGHT : MovingDirection.LEFT);
        } else if (targetData.vehicleId() == null) {
            var targetIsRight = closestVehiclePosition.getX() > targetData.contact().position().getX();
            state.getGunState().setRotatingDirection(targetIsRight ? MovingDirection.RIGHT : MovingDirection.LEFT);
            if (allowMove) {
                if (gunAngle < Math.PI / 3 && targetIsRight) {
                    state.setMovingDirection(MovingDirection.RIGHT);
                } else if (gunAngle > 2 * Math.PI / 3 && !targetIsRight) {
                    state.setMovingDirection(MovingDirection.LEFT);
                }
            }
        }
    }

    private boolean notSeparatedByOtherVehicle(double x, double objectX, Set<Position> otherVehiclePositions) {
        return otherVehiclePositions.stream()
                .map(Position::getX)
                .noneMatch(vehicleX -> vehicleX >= Math.min(x, objectX) && vehicleX <= Math.max(x, objectX));
    }

    private void switchShellIfNeeded(int vehicleId, VehicleState state, Set<ShellCalculations> shells) {
        String newSelectedShell = null;
        var ammo = state.getAmmo();
        var gunState = state.getGunState();
        if (state.getBomberState() != null && state.getBomberState().isReadyToFlight()) {
            var alreadyShot = shells.stream()
                    .anyMatch(shell -> ShellType.SGN.equals(shell.getModel().getSpecs().getType())
                                    && vehicleId == shell.getModel().getVehicleId());
            if (!alreadyShot) {
                var shellName = ShellSpecsPreset.LIGHT_SGN.getName();
                if (ammo.containsKey(shellName) && ammo.get(shellName) > 0) {
                    newSelectedShell = shellName;
                }
            }
        } else {
            var ammoSorted = ammo.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(entry -> new AmmoConfig()
                            .setName(entry.getKey())
                            .setAmount(entry.getValue()))
                    .sorted(Comparator.comparingInt(AmmoConfig::getAmount).reversed())
                    .toList();
            if (ammoSorted.size() > 1) {
                if (ammoSorted.get(0).getAmount() - ammoSorted.get(1).getAmount() > 4) {
                    newSelectedShell = ammoSorted.getFirst().getName();
                }
            }
        }
        if (newSelectedShell != null && !newSelectedShell.equals(gunState.getSelectedShell())) {
            gunState.setSelectedShell(newSelectedShell);
            gunState.setLoadedShell(null);
            gunState.setLoadingShell(null);
        }
    }

    private void launchDroneIfAvailable(VehicleModel model, BattleModel battleModel) {
        var droneState = model.getState().getDroneState();
        if (droneState != null && droneState.isReadyToLaunch() && !model.getState().isAboutToTurnOver()) {
            VehicleLaunchDroneProcessor.launch(model, battleModel);
        }
    }

    private void launchMissileConditional(VehicleModel model, BattleModel battleModel, boolean isCloseBattle) {
        var relativeHp = model.getRelativeHp();
        if (relativeHp < 0.7 && !isCloseBattle || relativeHp < 0.3) {
            VehicleLaunchMissileProcessor.launch(model, battleModel);
        }
    }

    private void runFromShellIfExists(VehicleModel model, Set<ShellCalculations> shells,
                              boolean moveRightBlocked, boolean moveLeftBlocked) {
        var vehiclePosition = model.getState().getPosition().getCenter();
        var maxRadius = model.getPreCalc().getMaxRadius();
        var vehicleArea = new Circle(vehiclePosition, maxRadius);
        var shellOptional = shells.stream()
                .filter(item -> {
                    var shellPosition = item.getPosition();
                    if (ShellType.SGN.equals(item.getModel().getSpecs().getType())
                            && item.getModel().getState().isStuck()) {
                        return Math.abs(shellPosition.getX() - vehiclePosition.getX()) < 3 * maxRadius;
                    }
                    var trajectoryVector = item.getVelocity().multiply(0.5);
                    if (trajectoryVector.magnitude() == 0) {
                        return false;
                    }
                    var trajectory = new Segment(shellPosition, shellPosition.shifted(trajectoryVector));
                    return !GeometryUtils.getSegmentAndCircleIntersectionPoints(trajectory, vehicleArea).isEmpty();
                })
                .findAny();
        shellOptional.ifPresent(shell -> {
            var shellPosition = shell.getPosition();
            var shellIsRight = shellPosition.getX() > vehiclePosition.getX();
            if (shellIsRight && !moveLeftBlocked) {
                model.getState().setMovingDirection(MovingDirection.LEFT);
            } else if (!shellIsRight && !moveRightBlocked) {
                model.getState().setMovingDirection(MovingDirection.RIGHT);
            }
        });
    }

    private void goAwayFromCloseVehicle(VehicleCalculations vehicle, Set<Position> otherVehiclePositions,
                                        boolean moveRightBlocked, boolean moveLeftBlocked) {
        var minDistance = vehicle.getModel().getPreCalc().getMaxRadius() * 3;
        var closestVehiclePosition = GeometryUtils.findClosestPosition(vehicle.getPosition(), otherVehiclePositions);
        if (closestVehiclePosition != null && closestVehiclePosition.distanceTo(vehicle.getPosition()) < minDistance) {
            var vehicleIsRight = closestVehiclePosition.getX() > vehicle.getPosition().getX();
            if (vehicleIsRight && !moveLeftBlocked) {
                vehicle.getModel().getState().setMovingDirection(MovingDirection.LEFT);
            }  else if (!vehicleIsRight && !moveRightBlocked) {
                vehicle.getModel().getState().setMovingDirection(MovingDirection.RIGHT);
            }
        }
    }

    private void controlJet(VehicleCalculations vehicle, BattleCalculations battle) {
        var verticalJet = vehicle.getModel().getConfig().getJet().getType().equals(JetType.VERTICAL);
        var state = vehicle.getModel().getState();
        var lowVelocity = state.getVelocity().getMovingVelocity().magnitude() < 1.0;
        // todo climbing
        var jetRelativeVolume = state.getJetState().getVolume() / vehicle.getModel().getConfig().getJet().getCapacity();
        var lowJet = jetRelativeVolume < 0.6;
        var highJet = jetRelativeVolume > 0.8;
        var zeroJet = jetRelativeVolume < 0.01;
        var needJetToMoveOrReturn = state.isTurnedOver() || state.getMovingDirection() != null && lowVelocity;
        if (state.getJetState().isActive() && (!needJetToMoveOrReturn || zeroJet)) {
            state.getJetState().setActive(false);
            //System.out.printf("%d: jet OFF, volume = %.6f\n", battle.getTime(), jetRelativeVolume);
        } else if (!state.getJetState().isActive() && !lowJet && needJetToMoveOrReturn) {
            state.getJetState().setActive(true);
            //System.out.printf("%d: jet ON, volume = %.6f\n", battle.getTime(), jetRelativeVolume);
        }
        if (verticalJet && highJet && getHeight(state.getPosition().getCenter(), battle.getModel().getRoom()) < 2.0) {
            state.setMovingDirection(null);
        }
    }

    private double getHeight(Position position, RoomModel roomModel) {
        var groundPosition = BattleUtils.getNearestGroundPosition(position.getX(), roomModel);
        return position.getY() - groundPosition.getY();
    }
}
