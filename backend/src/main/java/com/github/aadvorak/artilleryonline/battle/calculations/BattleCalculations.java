package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleCalculations {

    private BattleModel model;

    private Set<VehicleCalculations> vehicles;

    private Set<ShellCalculations> shells;

    private Set<MissileCalculations> missiles;

    private Set<DroneCalculations> drones;

    private Set<Calculations<?>> movingObjects;

    public BattleCalculations(BattleModel model) {
        this.model = model;
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
    }

    public Set<? extends Calculations<?>> getMovingObjects() {
        if (movingObjects == null) {
            movingObjects = new HashSet<>();
            movingObjects.addAll(vehicles);
            movingObjects.addAll(drones);
            movingObjects.addAll(shells);
            movingObjects.addAll(missiles);
        }
        return movingObjects;
    }
}
