package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class RoomState implements State, CompactSerializable {

    private List<Double> groundLine;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeCollection(groundLine, stream::writeDouble);
        return stream.toByteArray();
    }
}
