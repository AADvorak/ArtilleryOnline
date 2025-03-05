package com.github.aadvorak.artilleryonline.battle.events;

import com.github.aadvorak.artilleryonline.battle.common.ShellHitType;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShellHitEventObject implements CompactSerializable {

    private Integer id;

    private ShellHitType type;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeNullable(id, stream::writeInt);
        stream.writeSerializableValue(type);
    }
}
