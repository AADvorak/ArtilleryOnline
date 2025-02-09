package com.github.aadvorak.artilleryonline.battle.model;

import com.github.aadvorak.artilleryonline.battle.config.RoomConfig;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;
import com.github.aadvorak.artilleryonline.battle.state.RoomState;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;

public class RoomModel extends GenericSpecsConfigStateModel<RoomSpecs, RoomConfig, RoomState> implements CompactSerializable {
    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializable(getSpecs());
        stream.writeSerializable(getConfig());
        stream.writeSerializable(getState());
    }
}
