package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BattleModelResponse {

    private Map<Integer, ShellModel> shells;

    private Map<Integer, ExplosionModel> explosions;

    private RoomModel room;

    private Map<String, VehicleModel> vehicles;

    private boolean updated;
}
