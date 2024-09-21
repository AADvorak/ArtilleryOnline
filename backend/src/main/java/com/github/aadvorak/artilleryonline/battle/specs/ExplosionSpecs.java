package com.github.aadvorak.artilleryonline.battle.specs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ExplosionSpecs implements Specs {

    private double duration;

    private double radius;
}
