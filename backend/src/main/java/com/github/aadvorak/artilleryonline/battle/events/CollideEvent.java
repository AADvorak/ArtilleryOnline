package com.github.aadvorak.artilleryonline.battle.events;

import com.github.aadvorak.artilleryonline.battle.collision.CollideObjectType;
import com.github.aadvorak.artilleryonline.dto.response.CollisionResponse;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CollideEvent implements CompactSerializable {

    private CollisionResponse object;

    private int id;

    private CollideObjectType type;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(object);
        stream.writeInt(id);
        stream.writeSerializableValue(type);
    }
}
