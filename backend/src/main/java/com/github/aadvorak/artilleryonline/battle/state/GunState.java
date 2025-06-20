package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GunState implements State, CompactSerializable {

    private double angle;

    private double targetAngle;

    private MovingDirection rotatingDirection;

    private String loadedShell;

    private String selectedShell;

    private String loadingShell;

    private double loadRemainTime;

    private boolean triggerPushed = false;

    private boolean fixed = false;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(angle);
        stream.writeDouble(targetAngle);
        stream.writeSerializable(rotatingDirection);
        stream.writeNullable(loadedShell, stream::writeString);
        stream.writeNullable(selectedShell, stream::writeString);
        stream.writeNullable(loadingShell, stream::writeString);
        stream.writeDouble(loadRemainTime);
        stream.writeBoolean(triggerPushed);
        stream.writeBoolean(fixed);
    }
}
