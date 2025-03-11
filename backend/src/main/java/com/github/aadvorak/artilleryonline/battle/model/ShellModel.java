package com.github.aadvorak.artilleryonline.battle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.aadvorak.artilleryonline.battle.config.ShellConfig;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShellModel extends GenericSpecsConfigStateModel<ShellSpecs, ShellConfig, ShellState> implements CompactSerializable {

    private int id;

    @JsonIgnore
    private Long userId;

    @JsonIgnore
    private boolean updated = false;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(id);
        stream.writeSerializableValue(getSpecs());
        stream.writeSerializableValue(getState());
    }
}
