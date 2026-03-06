package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class BaseState implements State, CompactSerializable {

    private Map<String, Double> capturePoints = new HashMap<>();

    private Integer capturingTeamId;

    private boolean captured = false;

    public double getSumCapturePoints() {
        return capturePoints.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeMap(capturePoints, stream::writeString, stream::writeDouble);
        stream.writeNullable(capturingTeamId, stream::writeInt);
        stream.writeBoolean(captured);
    }
}
