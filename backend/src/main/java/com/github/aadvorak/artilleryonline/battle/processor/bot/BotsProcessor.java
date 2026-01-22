package com.github.aadvorak.artilleryonline.battle.processor.bot;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.BoxType;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.config.AmmoConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.ShellSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleLaunchDroneProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleLaunchMissileProcessor;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class BotsProcessor {

    private final TargetDataCalculator targetDataCalculator =  new TargetDataCalculator();

    public void process(Set<Integer> botVehicleIds, BattleCalculations battle) {
        battle.getVehicles().stream()
                .filter(vehicle -> botVehicleIds.contains(vehicle.getId()))
                .forEach(vehicle -> processVehicle(vehicle, battle));
    }

    private void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var state = vehicle.getModel().getState();
        var oldTriggerPushed = state.getGunState().isTriggerPushed();
        var oldGunRotatingDirection = state.getGunState().getRotatingDirection();
        var oldJetActive = state.getJetState().isActive();
        var oldMovingDirection = state.getMovingDirection();
        var vehicleAngle = state.getPosition().getAngle();
        var vehicleX = vehicle.getPosition().getX();
        var gunAngle = vehicleAngle + state.getGunState().getAngle();
        var needHp = vehicle.getModel().getRelativeHp() < 1.0;
        var needAmmo = vehicle.getModel().getRelativeAmmo() < 1.0;
        var isMovingToBox = false;
        var otherVehiclePositions = battle.getVehicles().stream()
                .filter(item -> !vehicle.getId().equals(item.getId()))
                .map(VehicleCalculations::getPosition)
                .collect(Collectors.toSet());
        var roomSpecs = battle.getModel().getRoom().getSpecs();
        var vehicleMaxRadius = vehicle.getModel().getPreCalc().getMaxRadius();
        var moveRightBlocked = roomSpecs.getRightTop().getX() - vehicleX < vehicleMaxRadius;
        var moveLeftBlocked = vehicleX - roomSpecs.getLeftBottom().getX() < vehicleMaxRadius;
        switchShellIfNeeded(vehicle.getId(), state, battle.getShells());
        launchDroneIfAvailable(vehicle.getModel(), battle.getModel());
        VehicleLaunchMissileProcessor.launch(vehicle.getModel(), battle.getModel());
        var targetData = targetDataCalculator.calculate(vehicle, battle);
        state.getGunState().setTriggerPushed(targetData != null && targetData.armor() != null);
        state.setMovingDirection(null);
        if (needHp) {
            isMovingToBox = setMovingToBoxIfAvailable(battle.getBoxes(), BoxType.HP, state, otherVehiclePositions);
        }
        if (needAmmo && !isMovingToBox) {
            isMovingToBox = setMovingToBoxIfAvailable(battle.getBoxes(), BoxType.AMMO, state, otherVehiclePositions);
        }
        if (gunAngle < Math.PI / 4) {
            state.getGunState().setRotatingDirection(MovingDirection.LEFT);
        } else if (gunAngle > 3 * Math.PI / 4) {
            state.getGunState().setRotatingDirection(MovingDirection.RIGHT);
        } else if (targetData == null) {
            state.getGunState().setRotatingDirection(gunAngle > Math.PI / 2 ? MovingDirection.RIGHT : MovingDirection.LEFT);
        } else if (targetData.armor() == null) {
            var closestPosition = GeometryUtils.findClosestPosition(targetData.contact().position(), otherVehiclePositions);
            if (closestPosition != null) {
                var targetIsRight = closestPosition.getX() > targetData.contact().position().getX();
                state.getGunState().setRotatingDirection(targetIsRight ? MovingDirection.RIGHT : MovingDirection.LEFT);
                if (!isMovingToBox) {
                    var minTargetDistance = vehicleMaxRadius * 5;
                    var vehicleTargetDistance = vehicleX - closestPosition.getX();
                    if (gunAngle < Math.PI / 3 && targetIsRight) {
                        state.setMovingDirection(MovingDirection.RIGHT);
                    } else if (gunAngle > 2 * Math.PI / 3 && !targetIsRight) {
                        state.setMovingDirection(MovingDirection.LEFT);
                    } else if (Math.abs(vehicleTargetDistance) < minTargetDistance) {
                        if (vehicleTargetDistance > 0 && !moveRightBlocked) {
                            state.setMovingDirection(MovingDirection.RIGHT);
                        }
                        if (vehicleTargetDistance < 0 && !moveLeftBlocked) {
                            state.setMovingDirection(MovingDirection.LEFT);
                        }
                    }
                }
            }
        }
        if (state.getMovingDirection() == null) {
            runFromShellIfExists(vehicle.getModel(), battle.getShells(), moveRightBlocked, moveLeftBlocked);
        }
        var lowVelocity = state.getVelocity().getMovingVelocity().magnitude() < 1.0;
        state.getJetState().setActive(state.isTurnedOver() || state.getMovingDirection() != null && lowVelocity);
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
        var hpBoxesPositions = boxes.stream()
                .filter(box -> boxType.equals(box.getModel().getSpecs().getType()))
                .map(BoxCalculations::getPosition)
                .filter(position -> notSeparatedByOtherVehicle(vehicleX, position.getX(), otherVehiclePositions))
                .collect(Collectors.toSet());
        var closestPosition = GeometryUtils.findClosestPosition(state.getPosition().getCenter(), hpBoxesPositions);
        if (closestPosition != null) {
            var xDiff = closestPosition.getX() - vehicleX;
            state.setMovingDirection(xDiff > 0 ? MovingDirection.RIGHT :  MovingDirection.LEFT);
            return true;
        }
        return false;
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
}
