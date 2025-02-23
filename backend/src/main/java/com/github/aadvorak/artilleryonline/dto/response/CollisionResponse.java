package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CollisionResponse implements CompactSerializable {

    private Integer vehicleId;

    private CollideObjectType type;

    public static CollisionResponse of(Collision collision) {
        return new CollisionResponse()
                .setType(collision.getType())
                .setVehicleId(collision.getSecondId());
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeNullable(vehicleId, stream::writeInt);
        stream.writeSerializableValue(type);
    }
}
