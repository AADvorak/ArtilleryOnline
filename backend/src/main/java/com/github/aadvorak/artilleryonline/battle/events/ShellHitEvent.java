package com.github.aadvorak.artilleryonline.battle.events;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShellHitEvent {

    private ShellHitEventObject object;

    private int shellId;
}
