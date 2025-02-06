package com.github.aadvorak.artilleryonline.battle.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoomConfig implements Config {

    private int background;

    private int groundTexture;
}
