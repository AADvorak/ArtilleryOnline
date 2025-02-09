package com.github.aadvorak.artilleryonline.battle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleModel extends GenericSpecsConfigStateModel<VehicleSpecs, VehicleConfig, VehicleState> implements CompactSerializable {

    private int id;

    @JsonIgnore
    private Long userId;

    private VehiclePreCalc preCalc;

    @JsonIgnore
    private boolean updated = false;

    @Override
    public byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        stream.writeInt(id);
        stream.writeSerializable(getSpecs());
        stream.writeSerializable(preCalc);
        stream.writeSerializable(getConfig());
        stream.writeSerializable(getState());
        stream.writeBoolean(updated);
        return stream.toByteArray();
    }
}
