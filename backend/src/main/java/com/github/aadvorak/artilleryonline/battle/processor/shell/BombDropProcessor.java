package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

import java.util.List;

public class BombDropProcessor {

    public static void drop(Position target, int vehicleId, BattleModel battleModel) {
        var vehicleModel = battleModel.getVehicles().values().stream()
                .filter(model -> model.getId() == vehicleId)
                .findAny().orElse(null);
        if (vehicleModel == null || vehicleModel.getConfig().getBomber() == null) {
            return;
        }
        var bomberState = vehicleModel.getState().getBomberState();
        if (bomberState == null || !bomberState.isReadyToFlight()) {
            return;
        }
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
        bomberState.setReadyToFlight(false);
        bomberState.setPrepareToFlightRemainTime(vehicleModel.getConfig().getBomber().getPrepareToFlightTime());
        bomberState.setRemainFlights(bomberState.getRemainFlights() - 1);
        vehicleModel.setUpdated(true);
    }
}
