package com.github.aadvorak.artilleryonline.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.events.BattleModelEvents;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelUpdates;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueueElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleUpdateResponse implements BattleUpdatesQueueElement {

    private String id;

    private long time;

    private BattleModelStateResponse state;

    private BattleModelUpdates updates;

    private BattleModelEvents events;
}
