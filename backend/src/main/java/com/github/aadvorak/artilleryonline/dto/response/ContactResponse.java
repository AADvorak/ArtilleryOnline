package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ContactResponse implements CompactSerializable {

    private Position position;

    private double angle;

    public static ContactResponse of(Contact contact) {
        return new ContactResponse()
                .setPosition(contact.position())
                .setAngle(contact.angle());
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(position);
        stream.writeDouble(angle);
    }
}
