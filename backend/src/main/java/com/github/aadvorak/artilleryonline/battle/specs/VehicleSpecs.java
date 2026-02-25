package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.battle.common.HitSurface;
import com.github.aadvorak.artilleryonline.battle.common.shapes.Shape;
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

    private int missiles;

    private int drones;

    private double minAngle;

    private double maxAngle;

    private double acceleration;

    private double wheelAngleVelocity;

    private Shape turretShape;

    private Map<HitSurface, Double> armor;

    private double wheelRadius;

    private double hullRadius;

    private double trackRepairTime;

    private double minTrackHitCaliber;

    private Map<String, GunSpecs> availableGuns;

    private Map<String, JetSpecs> availableJets;

    private Map<String, MissileLauncherSpecs> availableMissileLaunchers;

    private Map<String, DroneSpecs> availableDrones;

    private Map<String, BomberSpecs> availableBombers;

    private String defaultGun;

    private double minCollisionDamageImpact;

    private double collisionDamageCoefficient;

    private double explosionDamage;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(name);
        stream.writeDouble(hitPoints);
        stream.writeInt(missiles);
        stream.writeInt(drones);
        stream.writeDouble(minAngle);
        stream.writeDouble(maxAngle);
        stream.writeDouble(acceleration);
        stream.writeDouble(wheelAngleVelocity);
        stream.writeSerializableValue(turretShape);
        stream.writeMap(armor, value -> stream.writeString(value.name()), stream::writeDouble);
        stream.writeDouble(wheelRadius);
        stream.writeDouble(hullRadius);
        stream.writeDouble(trackRepairTime);
        stream.writeDouble(minTrackHitCaliber);
        stream.writeStringMapOfSerializable(availableGuns);
        stream.writeStringMapOfSerializable(availableJets);
        stream.writeStringMapOfSerializable(availableMissileLaunchers);
        stream.writeStringMapOfSerializable(availableDrones);
        stream.writeStringMapOfSerializable(availableBombers);
        stream.writeString(defaultGun);
    }
}
