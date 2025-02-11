package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GunState implements State, CompactSerializable {

    private String loadedShell;

    private String selectedShell;

    private String loadingShell;

    private double loadRemainTime;

    private boolean triggerPushed;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeNullable(loadedShell, stream::writeString);
        stream.writeNullable(selectedShell, stream::writeString);
        stream.writeNullable(loadingShell, stream::writeString);
        stream.writeDouble(loadRemainTime);
        stream.writeBoolean(triggerPushed);
    }
}
