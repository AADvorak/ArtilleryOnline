package com.github.aadvorak.artilleryonline.collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleQueueParams {

    private String selectedVehicle;
}
