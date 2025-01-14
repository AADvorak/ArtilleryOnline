package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.battle.common.JetType;
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

    private JetType type;
}
