package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class BattleCalculations {

    private BattleModel model;

    private Set<VehicleCalculations> vehicles;

    private Set<ShellCalculations> shells;

    private Set<MissileCalculations> missiles;

    private Set<DroneCalculations> drones;
}
