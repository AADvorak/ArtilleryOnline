package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.VehicleVelocity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleState implements State {

    private Position position;

    private MovingDirection movingDirection;

    // todo remove
    private double velocity;

    private VehicleVelocity vehicleVelocity = new VehicleVelocity();

    private double angle;

    private double gunAngle;

    private MovingDirection gunRotatingDirection;

    private double hitPoints;

    private Map<String, Integer> ammo;

    private GunState gunState;

    private TrackState trackState;
}
