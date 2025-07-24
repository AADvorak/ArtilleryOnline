package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public int getEnd() {
        return begin + groundLinePart.size() - 1;
    }

    public boolean intersects(RoomStateUpdate other) {
        var end = getEnd();
        var otherEnd = other.getEnd();
        return begin <= otherEnd && begin >= other.begin || end <= otherEnd && end >= other.begin;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(begin);
        stream.writeCollection(groundLinePart, stream::writeDouble);
    }

    public static RoomStateUpdate of(List<Double> groundLine, int begin, int end) {
        var roomStateUpdate = new RoomStateUpdate().setBegin(begin);
        for (int i = begin; i <= end; i++) {
            roomStateUpdate.getGroundLinePart().add(groundLine.get(i));
        }
        return roomStateUpdate;
    }
}
