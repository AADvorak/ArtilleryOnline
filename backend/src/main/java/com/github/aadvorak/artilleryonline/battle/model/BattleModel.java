package com.github.aadvorak.artilleryonline.battle.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class BattleModel {

    private RoomModel room;

    private Map<String, VehicleModel> vehicles;

    private List<ShellModel> shells;
}
