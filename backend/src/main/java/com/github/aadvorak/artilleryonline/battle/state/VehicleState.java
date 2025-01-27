package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.BodyAcceleration;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
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

    private BodyVelocity velocity = new BodyVelocity();

    private BodyAcceleration acceleration = new BodyAcceleration();

    private double angle;

    private double gunAngle;

    private MovingDirection gunRotatingDirection;

    private double hitPoints;

    private Map<String, Integer> ammo;

    private GunState gunState;

    private TrackState trackState;

    private JetState jetState;

    private boolean onGround;
}
