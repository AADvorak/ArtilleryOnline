package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueueElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleResponse implements BattleUpdatesQueueElement {

    private String id;

    private BattleModelResponse model;

    private long time;

    private boolean paused;

    private BattleStage battleStage;
}
