package com.github.aadvorak.artilleryonline.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BattleModelStateResponse {

    private List<ShellModelStateResponse> shells;

    private Map<String, VehicleModelStateResponse> vehicles;
}
