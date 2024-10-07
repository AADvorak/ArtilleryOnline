package com.github.aadvorak.artilleryonline.battle.tracking;

import com.github.aadvorak.artilleryonline.battle.Battle;

import java.util.Arrays;

public class BattleTracker {

    private final StringBuilder csvBuilder = new StringBuilder();

    public BattleTracker(Battle battle) {
        csvBuilder.append("time");
        battle.getModel().getVehicles().values().forEach(vehicle -> csvBuilder.append(
                String.format(",%d_position_x,%d_position_y,%d_angle,%d_velocity_x,%d_velocity_y,%d_velocity_angle",
                vehicle.getId(), vehicle.getId(), vehicle.getId(), vehicle.getId(), vehicle.getId(), vehicle.getId())));
        csvBuilder.append("\r\n");
    }

    public void appendToCsv(Battle battle) {
        csvBuilder.append(battle.getTime());
        battle.getModel().getVehicles().values().forEach(vehicle -> {
            var state = vehicle.getState();
            var position = state.getPosition();
            var velocity = state.getVelocity();
            appendNumbersToRow(
                    position.getX(),
                    position.getY(),
                    state.getAngle(),
                    velocity.getX(),
                    velocity.getY(),
                    velocity.getAngle()
            );
        });
        csvBuilder.append("\r\n");
    }

    public String getCsv() {
        return csvBuilder.toString();
    }

    private void appendNumbersToRow(double... numbers) {
        Arrays.stream(numbers).forEach(number -> csvBuilder.append(String.format(",\"%.3f\"", number)));
    }
}
