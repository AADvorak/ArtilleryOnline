package com.github.aadvorak.artilleryonline.dto.request;

import com.github.aadvorak.artilleryonline.battle.BattleType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleHistoryFiltersRequest {

    private BattleType battleType;
}
