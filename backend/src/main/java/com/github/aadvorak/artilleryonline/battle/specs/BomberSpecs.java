package com.github.aadvorak.artilleryonline.battle.specs;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BomberSpecs implements Specs, CompactSerializable {

    private int flights;

    private double prepareToFlightTime;

    private double flightTime;

    private ShellSpecs bombs;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(flights);
        stream.writeDouble(prepareToFlightTime);
        stream.writeDouble(flightTime);
        stream.writeSerializableValue(bombs);
    }
}
