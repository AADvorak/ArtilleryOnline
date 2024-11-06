package com.github.aadvorak.artilleryonline.battle.updates;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class RoomStateUpdate {

    private int begin;

    private final List<Double> groundLinePart = new ArrayList<>();
}
