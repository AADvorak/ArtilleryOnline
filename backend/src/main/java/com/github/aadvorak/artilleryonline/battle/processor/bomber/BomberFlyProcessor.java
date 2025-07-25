package com.github.aadvorak.artilleryonline.battle.processor.bomber;

import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.events.BomberFlyEvent;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class BomberFlyProcessor {

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
