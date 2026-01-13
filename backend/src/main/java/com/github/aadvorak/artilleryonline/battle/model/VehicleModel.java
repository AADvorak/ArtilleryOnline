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
public class VehicleModel
        extends BodyModel<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState>
        implements CompactSerializable {

    private int id;

    @JsonIgnore
    private Long userId;

    @JsonIgnore
    private Long turnedOverTime;

    @JsonIgnore
    private long lastSoundTime;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(id);
        stream.writeSerializableValue(getSpecs());
        stream.writeSerializableValue(getPreCalc());
        stream.writeSerializableValue(getConfig());
        stream.writeSerializableValue(getState());
    }

    @JsonIgnore
    public double getRelativeHp() {
        return getState().getHitPoints() / getSpecs().getHitPoints();
    }

    @JsonIgnore
    public double getRelativeAmmo() {
        return (float) getState().getAmmo().values().stream().reduce(0, Integer::sum)
                / (float) getConfig().getGun().getAmmo();
    }
}
