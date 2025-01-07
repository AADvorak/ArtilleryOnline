package com.github.aadvorak.artilleryonline.battle.events;

import com.github.aadvorak.artilleryonline.dto.response.CollisionResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleCollideEvent {

    private CollisionResponse object;

    private int vehicleId;
}
