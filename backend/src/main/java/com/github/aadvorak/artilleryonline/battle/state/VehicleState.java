package com.github.aadvorak.artilleryonline.battle.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleState implements State, CompactSerializable {

    private BodyPosition position = new BodyPosition();

    private BodyVelocity velocity = new BodyVelocity();

    private MovingDirection movingDirection;

    private BodyAcceleration acceleration = new BodyAcceleration();

    private double hitPoints;

    private Map<String, Integer> ammo;

    private Map<String, Integer> missiles;

    private GunState gunState;

    private TrackState trackState;

    private JetState jetState;

    private DroneInVehicleState droneState;

    private BomberState bomberState;

    private boolean onGround;

    @JsonIgnore
    public String getExistingAmmoShellName() {
        return ammo.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .findAny().orElse(null);
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(position);
        stream.writeSerializableValue(velocity);
        stream.writeSerializable(movingDirection);
        stream.writeDouble(hitPoints);
        stream.writeMap(ammo, stream::writeString, stream::writeInt);
        stream.writeMap(missiles, stream::writeString, stream::writeInt);
        stream.writeSerializable(gunState);
        stream.writeSerializable(trackState);
        stream.writeSerializable(jetState);
        stream.writeSerializable(droneState);
        stream.writeSerializable(bomberState);
        stream.writeBoolean(onGround);
    }
}
