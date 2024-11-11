package com.github.aadvorak.artilleryonline.battle.events;

import com.github.aadvorak.artilleryonline.battle.common.ShellHitType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShellHitEventObject {

    private Integer vehicleId;

    private ShellHitType type;
}
