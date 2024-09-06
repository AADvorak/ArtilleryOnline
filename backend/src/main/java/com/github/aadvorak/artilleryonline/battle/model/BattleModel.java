package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.common.IdGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class BattleModel {

    private final IdGenerator idGenerator = new IdGenerator();

    private double currentTimeStepSecs;

    private Map<Integer, ShellModel> shells;

    private RoomModel room;

    private Map<String, VehicleModel> vehicles;

    public void removeShellById(int id) {
        shells.remove(id);
    }

    public void removeVehicleById(int id) {
        vehicles.entrySet().stream()
                .filter(entry -> entry.getValue().getId() == id).findAny()
                .map(Map.Entry::getKey)
                .ifPresent(key -> vehicles.remove(key));
    }
}
