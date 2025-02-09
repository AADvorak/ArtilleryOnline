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

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeInt(id);
        stream.writeSerializable(getSpecs());
        stream.writeSerializable(getState());
        return stream.toByteArray();
    }
}
