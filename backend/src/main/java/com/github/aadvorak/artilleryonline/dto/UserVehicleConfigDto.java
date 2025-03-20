package com.github.aadvorak.artilleryonline.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class UserVehicleConfigDto {

    private String gun;

    private Map<String, Integer> ammo;
}
