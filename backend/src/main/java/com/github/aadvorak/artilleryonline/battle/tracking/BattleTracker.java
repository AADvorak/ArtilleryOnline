package com.github.aadvorak.artilleryonline.battle.tracking;

import com.github.aadvorak.artilleryonline.battle.Battle;

import java.util.Arrays;

public class BattleTracker {

    private final StringBuilder csvBuilder = new StringBuilder();

    public BattleTracker(Battle battle) {
        appendHeaderToCsv(battle);
    }

    public void appendToCsv(Battle battle) {
        if (battle.getModel().getUpdates().getRemoved() != null
                || battle.getModel().getUpdates().getAdded() != null) {
            appendHeaderToCsv(battle);
        }
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
        battle.getModel().getBoxes().values().forEach(box -> {
            var state = box.getState();
            var position = state.getPosition();
            var velocity = state.getVelocity();
            appendNumbersToRow(
                    position.getX(),
                    position.getY(),
                    position.getAngle(),
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

    private void appendHeaderToCsv(Battle battle) {
        csvBuilder.append("time");
        battle.getModel().getVehicles().values().forEach(vehicle ->
                csvBuilder.append(
                        ",{id}_x,{id}_y,{id}_angle,{id}_velocity_x,{id}_velocity_y,{id}_velocity_angle,{id}_gun_angle,{id}_target_angle"
                                .replace("{id}", String.valueOf(vehicle.getId()))));
        battle.getModel().getBoxes().values().forEach(box ->
                csvBuilder.append(",{id}_x,{id}_y,{id}_angle,{id}_velocity_x,{id}_velocity_y,{id}_velocity_angle"
                        .replace("{id}", String.valueOf(box.getId()))));
        csvBuilder.append("\r\n");
    }

    private void appendNumbersToRow(double... numbers) {
        Arrays.stream(numbers).forEach(number -> csvBuilder.append(String.format(",\"%.3f\"", number)));
    }
}
