package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.common.BoxType;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import com.github.aadvorak.artilleryonline.battle.specs.BoxSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoxSpecsPreset {

    HP("HP", new BoxSpecs()
            .setMass(0.2)
            .setType(BoxType.HP)
            .setShape(TrapezeShape.square(0.4))
    ),

    AMMO("AMMO", new BoxSpecs()
            .setMass(0.2)
            .setType(BoxType.AMMO)
            .setShape(TrapezeShape.square(0.4))
    );

    private final String name;

    private final BoxSpecs specs;
}
