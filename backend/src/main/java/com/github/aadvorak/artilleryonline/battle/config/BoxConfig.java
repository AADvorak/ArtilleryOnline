package com.github.aadvorak.artilleryonline.battle.config;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BoxConfig implements Config, CompactSerializable {

    private String color;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeNullable(color, stream::writeString);
    }
}
