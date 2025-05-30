package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.dto.response.BattleUpdateResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleAggregated {
    private boolean disabled = false;
    private BattleUpdateResponse update;
}
