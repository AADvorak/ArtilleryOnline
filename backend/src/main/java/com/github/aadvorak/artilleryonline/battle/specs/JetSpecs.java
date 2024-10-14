package com.github.aadvorak.artilleryonline.battle.specs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class JetSpecs {

    private double capacity;

    private double consumption;

    private double regeneration;

    private double acceleration;
}
