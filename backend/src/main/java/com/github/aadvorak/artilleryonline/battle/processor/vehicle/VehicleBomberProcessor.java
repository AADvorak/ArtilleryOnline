package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.events.BomberFlyEvent;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VehicleBomberProcessor extends VehicleProcessor implements BeforeStep1Processor {

    @Override
    protected void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var vehicleModel = vehicle.getModel();
        var battleModel = battle.getModel();
        var bomberState = vehicleModel.getState().getBomberState();
        if (bomberState == null || bomberState.isReadyToFlight()) {
            return;
        }
        if (bomberState.isFlying()) {
            bomberState.setFlightRemainTime(bomberState.getFlightRemainTime() - battleModel.getCurrentTimeStepSecs());
            if (bomberState.getFlightRemainTime() <= 0) {
                drop(vehicleModel, battleModel);
            }
            return;
        }
        bomberState.setPrepareToFlightRemainTime(bomberState.getPrepareToFlightRemainTime()
                - battleModel.getCurrentTimeStepSecs());
        if (bomberState.getPrepareToFlightRemainTime() <= 0) {
            bomberState.setReadyToFlight(true);
            vehicleModel.getUpdate().setUpdated();
        }
    }

    // todo move to separate class
    public static void fly(Position target, int vehicleId, BattleModel battleModel) {
        var vehicleModel = getVehicleModelWithBomber(vehicleId, battleModel);
        if (vehicleModel == null) {
            return;
        }
        var bomberState = vehicleModel.getState().getBomberState();
        if (bomberState == null || !bomberState.isReadyToFlight()) {
            return;
        }
        bomberState.setTarget(target);
        var roomSpecs = battleModel.getRoom().getSpecs();
        var direction = target.getX() > BattleUtils.getRoomWidth(roomSpecs) / 2
                ? MovingDirection.RIGHT : MovingDirection.LEFT;
        battleModel.getEvents().addBomberFly(new BomberFlyEvent().setMovingDirection(direction));
        bomberState.setReadyToFlight(false);
        bomberState.setFlying(true);
        bomberState.setFlightRemainTime(vehicleModel.getConfig().getBomber().getFlightTime());
        vehicleModel.getUpdate().setUpdated();
    }

    private void drop(VehicleModel vehicleModel, BattleModel battleModel) {
        var bomberState = vehicleModel.getState().getBomberState();
        var target = bomberState.getTarget();
        var roomSpecs = battleModel.getRoom().getSpecs();
        var y = 1.1 * BattleUtils.getRoomHeight(roomSpecs);
        var gravityAcceleration = roomSpecs.getGravityAcceleration();
        var height = y - target.getY();
        var specs = vehicleModel.getConfig().getBomber().getBombs();
        var velocityX = target.getX() > BattleUtils.getRoomWidth(roomSpecs) / 2
                ? specs.getVelocity() : -specs.getVelocity();
        var x = target.getX() - velocityX * Math.sqrt(2 * height / gravityAcceleration);
        var singleShift = specs.getRadius() * 2;
        var shifts = List.of(-singleShift, 0.0, singleShift);
        for (var shift : shifts) {
            var position = new Position().setX(x + shift).setY(y);
            var velocity = new Velocity().setX(velocityX);
            var state = new ShellState()
                    .setPosition(position)
                    .setVelocity(velocity);
            var id = battleModel.getIdGenerator().generate();
            var model = new ShellModel();
            model.setId(id);
            model.setUserId(vehicleModel.getUserId());
            model.setState(state);
            model.setSpecs(specs);
            battleModel.getShells().put(id, model);
            battleModel.getUpdates().addShell(model);
        }
        bomberState.setFlying(false);
        bomberState.setPrepareToFlightRemainTime(vehicleModel.getConfig().getBomber().getPrepareToFlightTime());
        bomberState.setRemainFlights(bomberState.getRemainFlights() - 1);
        vehicleModel.getUpdate().setUpdated();
    }

    private static VehicleModel getVehicleModelWithBomber(int vehicleId, BattleModel battleModel) {
        var vehicleModel = battleModel.getVehicles().values().stream()
                .filter(model -> model.getId() == vehicleId)
                .findAny().orElse(null);
        if (vehicleModel == null || vehicleModel.getConfig().getBomber() == null) {
            return null;
        }
        return vehicleModel;
    }
}
