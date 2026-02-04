package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleCalculations {

    private BattleModel model;

    private long time;

    private BattleType type;

    private Set<VehicleCalculations> vehicles;

    private Set<ShellCalculations> shells;

    private Set<MissileCalculations> missiles;

    private Set<DroneCalculations> drones;

    private Set<BoxCalculations> boxes;

    private Set<Calculations<?>> movingObjects;

    private Map<String, Integer> nicknameTeamMap = new HashMap<>();

    private Map<Integer, Integer> vehicleIdTeamMap = new HashMap<>();

    public BattleCalculations(Battle battle) {
        var model = battle.getModel();
        this.model = model;
        this.time = battle.getTime();
        this.type = battle.getType();
        vehicles = model.getVehicles().values().stream()
                .map(VehicleCalculations::new)
                .collect(Collectors.toSet());
        shells = model.getShells().values().stream()
                .map(ShellCalculations::new)
                .collect(Collectors.toSet());
        missiles = model.getMissiles().values().stream()
                .map(MissileCalculations::new)
                .collect(Collectors.toSet());
        drones = model.getDrones().values().stream()
                .map(DroneCalculations::new)
                .collect(Collectors.toSet());
        boxes = model.getBoxes().values().stream()
                .map(BoxCalculations::new)
                .collect(Collectors.toSet());
        nicknameTeamMap = battle.getNicknameTeamMap();
        vehicleIdTeamMap = new HashMap<>();
        nicknameTeamMap.forEach((key, value) -> {
            var vehicleModel = model.getVehicles().get(key);
            if (vehicleModel != null) {
                vehicleIdTeamMap.put(vehicleModel.getId(), value);
            }
        });
    }

    public Set<BoxCalculations> getBoxes() {
        if (model.getUpdates().getRemoved() == null || model.getUpdates().getRemoved().getBoxIds() == null) {
            return boxes;
        }
        return boxes.stream()
                .filter(box -> !model.getUpdates().getRemoved().getBoxIds().contains(box.getId()))
                .collect(Collectors.toSet());
    }

    public Set<DroneCalculations> getDrones() {
        if (model.getUpdates().getRemoved() == null || model.getUpdates().getRemoved().getDroneIds() == null) {
            return drones;
        }
        return drones.stream()
                .filter(drone -> !model.getUpdates().getRemoved().getDroneIds().contains(drone.getId()))
                .collect(Collectors.toSet());
    }

    public Set<ShellCalculations> getShells() {
        if (model.getUpdates().getRemoved() == null || model.getUpdates().getRemoved().getShellIds() == null) {
            return shells;
        }
        return shells.stream()
                .filter(shell -> !model.getUpdates().getRemoved().getShellIds().contains(shell.getId()))
                .collect(Collectors.toSet());
    }

    public Set<MissileCalculations> getMissiles() {
        if (model.getUpdates().getRemoved() == null || model.getUpdates().getRemoved().getMissileIds() == null) {
            return missiles;
        }
        return missiles.stream()
                .filter(missile -> !model.getUpdates().getRemoved().getMissileIds().contains(missile.getId()))
                .collect(Collectors.toSet());
    }

    public Set<? extends Calculations<?>> getMovingObjects() {
        if (movingObjects == null) {
            movingObjects = new HashSet<>();
            movingObjects.addAll(vehicles);
            movingObjects.addAll(getDrones());
            movingObjects.addAll(getShells());
            movingObjects.addAll(getMissiles());
            movingObjects.addAll(getBoxes());
        }
        return movingObjects;
    }

    public boolean allowedTarget(int vehicleId1, int vehicleId2) {
        return !Objects.equals(vehicleId1, vehicleId2)
                && (!type.isTeam()
                || !Objects.equals(vehicleIdTeamMap.get(vehicleId1), vehicleIdTeamMap.get(vehicleId2)));
    }
}
