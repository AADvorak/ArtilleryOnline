package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class DroneState implements BodyState, CompactSerializable {

    private BodyPosition position = new BodyPosition();

    private BodyVelocity velocity = new BodyVelocity();

    private MovingDirection pushingDirection;

    private MovingDirection rotatingDirection;

    private Map<String, Integer> ammo;

    private GunState gunState;

    private double gunAngle;

    private boolean destroyed = false;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(position);
        stream.writeSerializableValue(velocity);
        stream.writeMap(ammo, stream::writeString, stream::writeInt);
        stream.writeSerializableValue(gunState);
        stream.writeDouble(gunAngle);
        stream.writeBoolean(destroyed);
    }
}
