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
        stream.writeString(loadedShell);
        stream.writeString(selectedShell);
        stream.writeString(loadingShell);
        stream.writeDouble(loadRemainTime);
        stream.writeBoolean(triggerPushed);
    }
}
