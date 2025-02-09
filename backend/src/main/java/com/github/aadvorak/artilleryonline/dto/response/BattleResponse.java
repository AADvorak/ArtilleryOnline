package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueueElement;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleResponse implements BattleUpdatesQueueElement, CompactSerializable {

    private String id;

    private BattleModelResponse model;

    private long time;

    private int fps;

    private boolean paused;

    private BattleStage battleStage;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(id);
        stream.writeSerializable(model);
        stream.writeLong(time);
        stream.writeInt(fps);
        stream.writeBoolean(paused);
        stream.writeSerializable(battleStage);
    }
}
