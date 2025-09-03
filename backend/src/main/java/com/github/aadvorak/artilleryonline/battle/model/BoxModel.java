package com.github.aadvorak.artilleryonline.battle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.aadvorak.artilleryonline.battle.config.BoxConfig;
import com.github.aadvorak.artilleryonline.battle.precalc.BoxPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.BoxSpecs;
import com.github.aadvorak.artilleryonline.battle.state.BoxState;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxModel
        extends BodyModel<BoxSpecs, BoxPreCalc, BoxConfig, BoxState>
        implements CompactSerializable {

    private int id;

    @JsonIgnore
    private final TimeoutUpdate update = new TimeoutUpdate();

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(id);
        stream.writeSerializableValue(getSpecs());
        stream.writeSerializableValue(getPreCalc());
        stream.writeSerializableValue(getState());
    }
}
