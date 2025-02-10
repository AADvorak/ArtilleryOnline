package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.config.ExplosionConfig;
import com.github.aadvorak.artilleryonline.battle.specs.ExplosionSpecs;
import com.github.aadvorak.artilleryonline.battle.state.ExplosionState;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExplosionModel extends GenericSpecsConfigStateModel<ExplosionSpecs, ExplosionConfig, ExplosionState> implements CompactSerializable {

    private int id;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(id);
        stream.writeSerializableValue(getSpecs());
        stream.writeSerializableValue(getState());
    }
}
