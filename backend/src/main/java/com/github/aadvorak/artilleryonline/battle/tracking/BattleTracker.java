package com.github.aadvorak.artilleryonline.battle.tracking;

import com.github.aadvorak.artilleryonline.battle.Battle;

import java.util.Arrays;

public class BattleTracker {

    private final StringBuilder csvBuilder = new StringBuilder();

    public BattleTracker(Battle battle) {
        csvBuilder.append("time");
        battle.getModel().getVehicles().values().forEach(vehicle ->
                csvBuilder.append(
                ",{id}_position_x,{id}_position_y,{id}_angle,{id}_velocity_x,{id}_velocity_y,{id}_velocity_angle,{id}_gun_angle,{id}_target_angle"
                        .replace("{id}", String.valueOf(vehicle.getId()))));
        csvBuilder.append("\r\n");
    }

    public void appendToCsv(Battle battle) {
        csvBuilder.append(battle.getTime());
        battle.getModel().getVehicles().values().forEach(vehicle -> {
            var state = vehicle.getState();
            var position = state.getPosition();
            var velocity = state.getVelocity();
            var gunState = state.getGunState();
            appendNumbersToRow(
                    position.getX(),
                    position.getY(),
                    position.getAngle(),
                    velocity.getX(),
                    velocity.getY(),
                    velocity.getAngle(),
                    gunState.getAngle(),
                    gunState.getTargetAngle()
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
