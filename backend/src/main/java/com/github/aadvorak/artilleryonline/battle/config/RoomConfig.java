package com.github.aadvorak.artilleryonline.battle.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class RoomConfig implements Config {

    private List<Double> groundLine;
}
