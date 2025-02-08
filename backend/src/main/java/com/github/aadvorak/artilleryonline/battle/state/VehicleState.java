package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleState implements State {

    private BodyPosition position = new BodyPosition();

    private MovingDirection movingDirection;

    private BodyVelocity velocity = new BodyVelocity();

    private BodyAcceleration acceleration = new BodyAcceleration();

    private double gunAngle;

    private MovingDirection gunRotatingDirection;

    private double hitPoints;

    private Map<String, Integer> ammo;

    private Map<String, Integer> missiles;

    private GunState gunState;

    private TrackState trackState;

    private JetState jetState;

    private boolean onGround;
}
