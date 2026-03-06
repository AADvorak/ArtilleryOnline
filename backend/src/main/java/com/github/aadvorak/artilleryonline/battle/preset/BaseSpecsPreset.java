package com.github.aadvorak.artilleryonline.battle.preset;

import com.github.aadvorak.artilleryonline.battle.specs.BaseSpecs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseSpecsPreset {

    NEUTRAL("Neutral", new BaseSpecs()
            .setCapturePoints(100.0)
            .setCaptureRate(10.0)
            .setRadius(1.0)
    );

    private final String name;

    private final BaseSpecs specs;
}
