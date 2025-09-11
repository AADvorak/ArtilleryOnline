package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public enum ShellHitType implements CompactSerializable {
    GROUND, VEHICLE_HULL, VEHICLE_TRACK, DRONE, BOX;

    public static ShellHitType of(Calculations<?> calculations) {
        if (calculations == null) return GROUND;
        if (calculations instanceof VehicleCalculations) return VEHICLE_HULL;
        if (calculations instanceof WheelCalculations) return VEHICLE_TRACK;
        if (calculations instanceof DroneCalculations) return DRONE;
        if (calculations instanceof BoxCalculations) return BOX;
        return null;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(this.name());
    }
}
