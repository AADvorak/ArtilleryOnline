package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.BomberSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BomberSpecsPreset {

    DEFAULT("Bomber", new BomberSpecs()
            .setFlights(3)
            .setPrepareToFlightTime(30.0)
            .setFlightTime(3.0)
            .setBombs(ShellSpecsPreset.BMB.getSpecs())
    );

    private final String name;

    private final BomberSpecs specs;
}
