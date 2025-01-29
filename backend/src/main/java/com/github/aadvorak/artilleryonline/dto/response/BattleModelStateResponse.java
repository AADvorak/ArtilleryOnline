package com.github.aadvorak.artilleryonline.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.state.MissileState;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelStateResponse {

    private Map<String, VehicleState> vehicles;

    private Map<Integer, ShellState> shells;

    private Map<Integer, MissileState> missiles;
}
