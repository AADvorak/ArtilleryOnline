package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class DroneSpecs implements Specs, CompactSerializable {

    private double maxEngineAcceleration;

    private double hullRadius;

    private double enginesRadius;

    private double mass;

    private double flyHeight;

    private double criticalAngle;

    private double prepareToLaunchTime;

    private Map<String, GunSpecs> availableGuns;

    private double minCollisionDestroyImpact;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(maxEngineAcceleration);
        stream.writeDouble(hullRadius);
        stream.writeDouble(enginesRadius);
        stream.writeDouble(mass);
        stream.writeDouble(flyHeight);
        stream.writeDouble(criticalAngle);
        stream.writeDouble(prepareToLaunchTime);
        stream.writeStringMapOfSerializable(availableGuns);
    }
}
