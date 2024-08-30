package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.common.IdGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleModel {

    private IdGenerator idGenerator = new IdGenerator();

    private RoomModel room;

    private Map<String, VehicleModel> vehicles;

    private List<ShellModel> shells;

    public void removeShellById(int id) {
        shells = shells.stream()
                .filter(shellModel -> shellModel.getId() != id)
                .collect(Collectors.toList());
    }

    public void removeVehicleById(int id) {
        vehicles.entrySet().stream()
                .filter(entry -> entry.getValue().getId() == id).findAny()
                .map(Map.Entry::getKey)
                .ifPresent(key -> vehicles.remove(key));
    }
}
