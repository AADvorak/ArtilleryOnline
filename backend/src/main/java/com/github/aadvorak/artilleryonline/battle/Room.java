package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class Room {

    private BattleParticipant owner;

    private Map<Long, BattleParticipant> guests = new HashMap<>();

    private Battle battle;
}
