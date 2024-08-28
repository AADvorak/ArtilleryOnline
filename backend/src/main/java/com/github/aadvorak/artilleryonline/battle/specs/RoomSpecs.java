package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoomSpecs implements Specs {

    private Position leftBottom;

    private Position rightTop;

    private double step;
}
