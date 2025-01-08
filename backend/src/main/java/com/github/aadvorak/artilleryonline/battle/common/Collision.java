package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.CollisionPair;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Collision {

    private Integer vehicleId;

    private CollideObjectType type;

    private CollisionPair pair;

    private double interpenetration;

    private double angle;

    public static Collision withGround(Calculations first, double interpenetration, double angle) {
        return new Collision()
                .setType(CollideObjectType.GROUND)
                .setPair(new CollisionPair(first, null))
                .setInterpenetration(interpenetration)
                .setAngle(angle);
    }

    public static Collision ofVehicles(Calculations first, Calculations second, double interpenetration) {
        return new Collision()
                .setVehicleId(second.getVehicleId())
                .setType(CollideObjectType.VEHICLE)
                .setPair(new CollisionPair(first, second))
                .setInterpenetration(interpenetration)
                .setAngle(getCollisionAngle(first.getPosition(), second.getPosition()));
    }

    public static Collision inverted(Collision collision) {
        return new Collision()
                .setVehicleId(collision.getPair().first().getVehicleId())
                .setType(collision.getType())
                .setPair(new CollisionPair(collision.getPair().second(), collision.getPair().first()))
                .setInterpenetration(collision.getInterpenetration())
                .setAngle(collision.getAngle() + Math.PI);
    }

    private static double getCollisionAngle(Position position, Position otherPosition) {
        var sign = Math.signum(otherPosition.getX() - position.getX());
        return Math.asin((position.getY() - otherPosition.getY())
                / position.distanceTo(otherPosition)) + sign * Math.PI / 2;
    }
}
