package com.github.aadvorak.artilleryonline.battle.specs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShellSpecs implements Specs {

    private double velocity;

    private double damage;

    private double radius;
}
