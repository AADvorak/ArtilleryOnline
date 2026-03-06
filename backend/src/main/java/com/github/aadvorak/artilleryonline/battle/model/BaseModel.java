package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.config.BaseConfig;
import com.github.aadvorak.artilleryonline.battle.specs.BaseSpecs;
import com.github.aadvorak.artilleryonline.battle.state.BaseState;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class BaseModel
        extends GenericSpecsConfigStateModel<BaseSpecs, BaseConfig, BaseState>
        implements CompactSerializable {

    private int id;

    private boolean updated = false;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeInt(id);
        stream.writeSerializableValue(getSpecs());
        stream.writeSerializableValue(getConfig());
        stream.writeSerializableValue(getState());
    }

    public void removeNotOnBaseCapturePoints(Set<String> nicknamesOnBase) {
        if (getState().getCapturePoints().entrySet().removeIf(entry ->
                !nicknamesOnBase.contains(entry.getKey()))) {
            this.updated = true;
        }
    }

    public void addOnBaseCapturePoints(Set<String> nicknamesOnBase) {
        nicknamesOnBase.forEach(nickname -> {
            if (!getState().getCapturePoints().containsKey(nickname)) {
                getState().getCapturePoints().put(nickname, 0.0);
                this.updated = true;
            }
        });
    }

    public void setCapturingTeamId(Integer capturingTeamId) {
        getState().setCapturingTeamId(capturingTeamId);
        this.updated = true;
    }

    public void increaseCapturePoints(double timeStep) {
        for (var entry : getState().getCapturePoints().entrySet()) {
            entry.setValue(entry.getValue() + getSpecs().getCaptureRate() * timeStep);
        }
    }

    public void setCapturedIfEnoughPoints() {
        if (getState().getSumCapturePoints() >= getSpecs().getCapturePoints()) {
            getState().setCaptured(true);
            this.updated = true;
        }
    }

    public void resetUpdated() {
        this.updated = false;
    }
}
