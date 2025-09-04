package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.common.Shift;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import com.github.aadvorak.artilleryonline.battle.specs.BoxSpecs;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

@Getter
public class BoxPreCalc implements BodyPreCalc, CompactSerializable {

    public BoxPreCalc(BoxSpecs specs) {
        mass = specs.getMass();
        // todo
        var radius = getRadius(specs);
        var density = mass / (Math.pow(radius, 2) * Math.PI);
        momentOfInertia = density * Math.PI * Math.pow(radius, 4);
        centerOfMassShift = new Shift(((TrapezeShape) specs.getShape()).getHeight() / 2, Math.PI / 2);
        maxRadius = radius * Math.sqrt(2);
    }

    private final double mass;

    private final double momentOfInertia;

    private final Shift centerOfMassShift;

    private final double maxRadius;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(mass);
        stream.writeDouble(momentOfInertia);
        stream.writeSerializableValue(centerOfMassShift);
        stream.writeDouble(maxRadius);
    }

    private double getRadius(BoxSpecs specs) {
        if (specs.getShape() instanceof TrapezeShape trapeze) {
            return (trapeze.getBottomRadius() + trapeze.getTopRadius() + trapeze.getHeight() / 2) / 3;
        }
        return 1.0;
    }
}
