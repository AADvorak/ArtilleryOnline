package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public enum ShellHitType implements CompactSerializable {
    GROUND, VEHICLE_HULL, VEHICLE_TRACK;

    public static ShellHitType of(Calculations<?> calculations) {
        if (calculations == null) return GROUND;
        if (calculations instanceof VehicleCalculations) return VEHICLE_HULL;
        if (calculations instanceof WheelCalculations) return VEHICLE_TRACK;
        return null;
    }

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeString(this.name());
        return stream.toByteArray();
    }
}
