package com.github.aadvorak.artilleryonline.battle.config;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoomConfig implements Config, CompactSerializable {

    private int background;

    private int groundTexture;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(background);
        stream.writeInt(groundTexture);
    }
}
