package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BomberState implements State, CompactSerializable {

    private boolean readyToFlight = true;

    private double prepareToFlightRemainTime;

    private int remainFlights;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeBoolean(this.readyToFlight);
        stream.writeDouble(this.prepareToFlightRemainTime);
        stream.writeInt(this.remainFlights);
    }
}
