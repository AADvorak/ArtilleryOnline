package com.github.aadvorak.artilleryonline.battle.config;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BaseConfig implements Config, CompactSerializable {

    private Integer teamId;

    private double positionX;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeNullable(teamId, stream::writeInt);
        stream.writeDouble(positionX);
    }
}
