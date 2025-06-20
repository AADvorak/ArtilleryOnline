package com.github.aadvorak.artilleryonline.battle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.aadvorak.artilleryonline.battle.config.MissileConfig;
import com.github.aadvorak.artilleryonline.battle.specs.MissileSpecs;
import com.github.aadvorak.artilleryonline.battle.state.MissileState;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissileModel extends GenericSpecsConfigStateModel<MissileSpecs, MissileConfig, MissileState> implements CompactSerializable {

    private int id;

    private int vehicleId;

    @JsonIgnore
    private Long userId;

    @JsonIgnore
    private final TimeoutUpdate update = new TimeoutUpdate();

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(id);
        stream.writeInt(vehicleId);
        stream.writeSerializableValue(getSpecs());
        stream.writeSerializableValue(getState());
    }
}
