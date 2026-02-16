package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueueElement;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class BattleResponse implements BattleUpdatesQueueElement, CompactSerializable {

    private String id;

    private BattleModelResponse model;

    private long time;

    private long duration;

    private int fps;

    private boolean paused;

    private BattleStage battleStage;

    private BattleType type;

    private Map<String, Integer> nicknameTeamMap = new HashMap<>();

    private Set<String> userNicknames = new HashSet<>();

    private Map<String, String> playerVehicleNameMap;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeString(id);
        stream.writeSerializableValue(model);
        stream.writeLong(time);
        stream.writeLong(duration);
        stream.writeInt(fps);
        stream.writeBoolean(paused);
        stream.writeSerializableValue(battleStage);
        stream.writeSerializableValue(type);
        stream.writeMap(nicknameTeamMap, stream::writeString, stream::writeInt);
        stream.writeCollection(userNicknames, stream::writeString);
        stream.writeMap(playerVehicleNameMap, stream::writeString, stream::writeString);
    }
}
