package com.github.aadvorak.artilleryonline.battle.state;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class JetState implements State {

    private double volume;

    private boolean active;
}
