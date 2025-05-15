package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.Vector;

public record BodyForce(Force moving, Force rotating, Vector radiusVector, String description) {

    public static BodyForce of(Force force, Vector radiusVector, String description) {
        var moving = Force.of(force.projectionOnto(radiusVector));
        var rotating = new Force()
                .setX(force.getX() - moving.getX())
                .setY(force.getY() - moving.getY());
        return new BodyForce(moving, rotating, radiusVector, description);
    }

    public static BodyForce atCOM(Force force, String description) {
        return new BodyForce(force, null,null, description);
    }

    public static BodyForce zero(String description) {
        return new BodyForce(null, null, null, description);
    }
}
