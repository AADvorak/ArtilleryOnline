package com.github.aadvorak.artilleryonline.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.events.BattleModelEvents;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelUpdates;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueueElement;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleUpdateResponse implements BattleUpdatesQueueElement, CompactSerializable {

    private String id;

    private long time;

    private int fps;

    private BattleStage stage;

    private BattleModelStateResponse state;

    private BattleModelUpdates updates;

    private BattleModelEvents events;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeLong(time);
        stream.writeInt(fps);
        stream.writeSerializable(stage);
        stream.writeSerializable(state);
        stream.writeSerializable(updates);
        stream.writeSerializable(events);
    }
}
