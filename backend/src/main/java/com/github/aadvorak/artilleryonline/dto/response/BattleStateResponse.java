package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueueElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleStateResponse implements BattleUpdatesQueueElement {

    private String id;

    private long time;

    private BattleModelStateResponse state;
}
