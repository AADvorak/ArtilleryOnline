package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Vector;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class BodyForce {

    private Force moving;

    private Force rotating;

    private Vector radiusVector;

    private String description;

    public Force moving() {
        return moving;
    }

    public Force rotating() {
        return rotating;
    }

    public Vector radiusVector() {
        return radiusVector;
    }

    public String description() {
        return description;
    }

    public double torque() {
        if (radiusVector == null || rotating == null) {
            return 0;
        }
        return radiusVector.vectorProduct(rotating);
    }

    public static BodyForce of(Force force, Position position, Position comPosition, String description) {
        var radiusVector = comPosition.vectorTo(position);
        var moving = Force.of(force.projectionOnto(radiusVector));
        var rotating = new Force()
                .setX(force.getX() - moving.getX())
                .setY(force.getY() - moving.getY());
        return new BodyForce(moving, rotating, radiusVector, description);
    }

    public static BodyForce atCOM(Force force, String description) {
        return new BodyForce(force, null,null, description);
    }

    @Override
    public String toString() {
        return "BodyForce{" +
                "moving=" + moving +
                ", rotating=" + rotating +
                ", radiusVector=" + radiusVector +
                ", description='" + description + '\'' +
                '}';
    }
}
