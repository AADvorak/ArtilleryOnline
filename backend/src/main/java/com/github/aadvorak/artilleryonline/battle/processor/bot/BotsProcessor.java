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
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.ShellSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleLaunchDroneProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleLaunchMissileProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleMissileLauncherProcessor;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

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
        switchToSignalShellIfAvailable(vehicle.getId(), state, battle.getShells());
        launchDroneIfAvailable(vehicle.getModel(), battle.getModel());
        VehicleLaunchMissileProcessor.launch(vehicle.getModel(), battle.getModel());
        var targetData = targetDataCalculator.calculate(vehicle, battle);
        state.getGunState().setTriggerPushed(targetData != null && targetData.armor() != null); // todo check penetration or use HE
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

    private void switchToSignalShellIfAvailable(int vehicleId, VehicleState state, Set<ShellCalculations> shells) {
        if (state.getBomberState() != null && state.getBomberState().isReadyToFlight()) {
            var alreadyShot = shells.stream()
                    .anyMatch(shell -> ShellType.SGN.equals(shell.getModel().getSpecs().getType())
                                    && vehicleId == shell.getModel().getVehicleId());
            if (!alreadyShot) {
                var shellName = ShellSpecsPreset.LIGHT_SGN.getName();
                var ammo = state.getAmmo();
                var gunState = state.getGunState();
                if (!shellName.equals(gunState.getSelectedShell())
                        && ammo.containsKey(shellName) && ammo.get(shellName) > 0) {
                    gunState.setSelectedShell(shellName);
                    gunState.setLoadedShell(null);
                    gunState.setLoadingShell(null);
                }
            }
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
        var vehicleArea = new Circle(vehiclePosition, model.getPreCalc().getMaxRadius());
        var shellOptional = shells.stream()
                .filter(item -> {
                    var trajectoryVector = item.getVelocity().multiply(0.5);
                    if (trajectoryVector.magnitude() == 0) {
                        return false;
                    }
                    var shellPosition = item.getPosition();
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
