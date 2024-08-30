package com.github.aadvorak.artilleryonline.battle.command;

import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DirectionCommandParams {

    private MovingDirection direction;
}
