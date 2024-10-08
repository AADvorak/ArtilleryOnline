package com.github.aadvorak.artilleryonline.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleStateResponse {

    private String id;

    private long time;

    private BattleModelStateResponse state;
}
