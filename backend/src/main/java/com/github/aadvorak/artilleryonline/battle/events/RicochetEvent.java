package com.github.aadvorak.artilleryonline.battle.events;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RicochetEvent {

    private int shellId;
}
