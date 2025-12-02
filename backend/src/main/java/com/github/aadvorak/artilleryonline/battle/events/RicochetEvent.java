package com.github.aadvorak.artilleryonline.battle.events;

import com.github.aadvorak.artilleryonline.dto.response.ContactResponse;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RicochetEvent implements CompactSerializable {

    private int shellId;

    private ContactResponse contact;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(shellId);
        stream.writeSerializableValue(contact);
    }
}
