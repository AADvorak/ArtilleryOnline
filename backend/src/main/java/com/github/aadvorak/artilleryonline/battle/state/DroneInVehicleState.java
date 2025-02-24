package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DroneInVehicleState implements State, CompactSerializable {

    private boolean launched = false;

    private boolean readyToLaunch = true;

    private double prepareToLaunchRemainTime;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeBoolean(this.launched);
        stream.writeBoolean(this.readyToLaunch);
        stream.writeDouble(this.prepareToLaunchRemainTime);
    }
}
