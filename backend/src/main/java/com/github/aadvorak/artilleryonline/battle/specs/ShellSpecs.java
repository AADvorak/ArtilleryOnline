package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.battle.common.ShellType;
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

    // todo replace with mass
    @Deprecated()
    private double pushCoefficient;

    private double mass;

    private double caliber;

    private ShellType type;
}
