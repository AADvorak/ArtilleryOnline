package com.github.aadvorak.artilleryonline.battle.updates;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class RoomStateUpdate implements CompactSerializable {

    private int begin;

    private final List<Double> groundLinePart = new ArrayList<>();

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(begin);
        stream.writeCollection(groundLinePart, stream::writeDouble);
    }
}
