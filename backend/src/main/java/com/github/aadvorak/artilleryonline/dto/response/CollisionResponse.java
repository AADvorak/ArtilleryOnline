package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.collision.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CollisionResponse implements CompactSerializable {

    private Integer id;

    private CollideObjectType type;

    public static CollisionResponse of(Collision collision) {
        return new CollisionResponse()
                .setType(collision.getType())
                .setId(collision.getSecondId());
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeNullable(id, stream::writeInt);
        stream.writeSerializableValue(type);
    }
}
