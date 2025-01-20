package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShellState implements State {

    private Position position;

    private Velocity velocity;
}
