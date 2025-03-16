package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BomberState implements State, CompactSerializable {

    private boolean flying = false;

    private boolean readyToFlight = true;

    private double prepareToFlightRemainTime;

    private double flightRemainTime;

    private Position target;

    private int remainFlights;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeBoolean(flying);
        stream.writeBoolean(readyToFlight);
        stream.writeInt(remainFlights);
    }
}
