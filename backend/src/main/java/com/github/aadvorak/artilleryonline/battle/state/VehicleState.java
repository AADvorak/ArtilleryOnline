package com.github.aadvorak.artilleryonline.battle.state;

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

    private double gunAngle;

    private MovingDirection gunRotatingDirection;

    private double hitPoints;

    private Map<String, Integer> ammo;

    private Map<String, Integer> missiles;

    private GunState gunState;

    private TrackState trackState;

    private JetState jetState;

    private boolean onGround;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(position);
        stream.writeSerializableValue(velocity);
        stream.writeSerializable(movingDirection);
        stream.writeDouble(gunAngle);
        stream.writeSerializable(gunRotatingDirection);
        stream.writeDouble(hitPoints);
        stream.writeMap(ammo, stream::writeString, stream::writeInt);
        stream.writeMap(missiles, stream::writeString, stream::writeInt);
        stream.writeSerializable(gunState);
        stream.writeSerializable(trackState);
        stream.writeSerializable(jetState);
        stream.writeBoolean(onGround);
    }
}
