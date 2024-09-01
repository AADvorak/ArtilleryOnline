package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.BattleStage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleStateResponse {

    private BattleModelStateResponse model;

    private long time;

    private BattleStage battleStage;
}
