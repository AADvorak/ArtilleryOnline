package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerBattleStatisticsResponse implements CompactSerializable {

    private double causedDamage;

    private int destroyedVehicles;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeDouble(causedDamage);
        stream.writeInt(destroyedVehicles);
    }
}
