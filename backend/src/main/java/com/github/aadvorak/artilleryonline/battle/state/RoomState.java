package com.github.aadvorak.artilleryonline.battle.state;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class RoomState implements State {

    private List<Double> groundLine;
}
