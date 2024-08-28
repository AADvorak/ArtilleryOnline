package com.github.aadvorak.artilleryonline.battle.config;

import com.github.aadvorak.artilleryonline.battle.common.Ground;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoomConfig implements Config {

    private Ground ground;
}
