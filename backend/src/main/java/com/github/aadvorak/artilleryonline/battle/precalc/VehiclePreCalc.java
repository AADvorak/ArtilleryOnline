package com.github.aadvorak.artilleryonline.battle.precalc;

import com.github.aadvorak.artilleryonline.battle.common.Shift;
import com.github.aadvorak.artilleryonline.battle.common.shapes.HalfCircleShape;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;

@Getter
public class VehiclePreCalc implements BodyPreCalc, CompactSerializable {

    public VehiclePreCalc(VehicleSpecs specs) {
        var comDistance = getComDistance(specs);
        wheelDistance = Math.sqrt(Math.pow(specs.getWheelRadius() + Math.abs(comDistance), 2)
                + Math.pow(specs.getHullRadius(), 2));
        wheelAngle = Math.atan((specs.getWheelRadius() + comDistance) / specs.getHullRadius());
        mass = getTurretMass(specs) + getWheelsMass(specs);
        maxRadius = wheelDistance + specs.getWheelRadius();
        momentOfInertia = getMomentOfInertia(specs);
        centerOfMassShift = new Shift(comDistance, Math.PI / 2);
    }

    private final double wheelDistance;

    private final double wheelAngle;

    private final double mass;

    private final double momentOfInertia;

    private final Shift centerOfMassShift;

    private final double maxRadius;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(wheelDistance);
        stream.writeDouble(wheelAngle);
        stream.writeDouble(mass);
        stream.writeDouble(momentOfInertia);
        stream.writeSerializableValue(centerOfMassShift);
        stream.writeDouble(maxRadius);
    }

    private double getComDistance(VehicleSpecs specs) {
        return (getWheelsMomentOfMass(specs) + getTurretMomentOfMass(specs))
                / (getWheelsMass(specs) + getTurretMass(specs));
    }

    private double getWheelsMass(VehicleSpecs specs) {
        return 2 * Math.PI * Math.pow(specs.getWheelRadius(), 2);
    }

    private double getWheelsMomentOfMass(VehicleSpecs specs) {
        return - 2 * Math.PI * Math.pow(specs.getWheelRadius(), 3);
    }

    private double getTurretMass(VehicleSpecs specs) {
        if (specs.getTurretShape() instanceof HalfCircleShape halfCircleShape) {
            return Math.PI * Math.pow(halfCircleShape.getRadius(), 2) / 2;
        }
        if (specs.getTurretShape() instanceof TrapezeShape trapezeShape) {
            var bottomRadius = trapezeShape.getBottomRadius();
            var topRadius = trapezeShape.getTopRadius();
            var height = trapezeShape.getHeight();
            return height * (bottomRadius + topRadius);
        }
        return 0.0;
    }

    private double getTurretMomentOfMass(VehicleSpecs specs) {
        if (specs.getTurretShape() instanceof HalfCircleShape halfCircleShape) {
            return 2 * Math.pow(halfCircleShape.getRadius(), 3) / 3;
        }
        if (specs.getTurretShape() instanceof TrapezeShape trapezeShape) {
            var bottomRadius = trapezeShape.getBottomRadius();
            var topRadius = trapezeShape.getTopRadius();
            var height = trapezeShape.getHeight();
            return Math.pow(height, 2) * (bottomRadius + 2 * topRadius) / 3;
        }
        return 0.0;
    }

    private double getMomentOfInertia(VehicleSpecs specs) {
        // todo write accurate formulas
        if (specs.getTurretShape() instanceof HalfCircleShape halfCircleShape) {
            return 2 * Math.PI * Math.pow(halfCircleShape.getRadius(), 4);
        }
        if (specs.getTurretShape() instanceof TrapezeShape trapezeShape) {
            return 2 * Math.PI * Math.pow((trapezeShape.getBottomRadius() + trapezeShape.getHeight()) / 2, 4);
        }
        return 0.0;
    }
}
