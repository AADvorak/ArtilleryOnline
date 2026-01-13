package com.github.aadvorak.artilleryonline.dto;

import com.github.aadvorak.artilleryonline.battle.config.AmmoConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class UserVehicleConfigDto {

    private String gun;

    private List<AmmoConfig> ammo;
}
