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
public class VehicleSpecs implements Specs, CompactSerializable {

    private String name;

    private double hitPoints;

    private int ammo;

    private int missiles;

    private double minAngle;

    private double maxAngle;

    private double acceleration;

    private double radius;

    private double wheelRadius;

    private double hullRadius;

    private double trackRepairTime;

    private double minTrackHitCaliber;

    private Map<String, GunSpecs> availableGuns;

    private Map<String, JetSpecs> availableJets;

    private Map<String, MissileSpecs> availableMissiles;

    private Map<String, DroneSpecs> availableDrones;

    private Map<String, ShellSpecs> availableBombs;

    private double minCollisionDamageImpact;

    private double collisionDamageCoefficient;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(name);
        stream.writeDouble(hitPoints);
        stream.writeInt(ammo);
        stream.writeInt(missiles);
        stream.writeDouble(minAngle);
        stream.writeDouble(maxAngle);
        stream.writeDouble(acceleration);
        stream.writeDouble(radius);
        stream.writeDouble(wheelRadius);
        stream.writeDouble(hullRadius);
        stream.writeDouble(trackRepairTime);
        stream.writeDouble(minTrackHitCaliber);
        stream.writeStringMapOfSerializable(availableGuns);
        stream.writeStringMapOfSerializable(availableJets);
        stream.writeStringMapOfSerializable(availableMissiles);
        stream.writeStringMapOfSerializable(availableDrones);
    }
}
