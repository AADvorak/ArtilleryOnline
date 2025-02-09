package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TrackState implements State, CompactSerializable {

    private boolean broken = false;

    private double repairRemainTime;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeBoolean(broken);
        stream.writeDouble(repairRemainTime);
    }
}
