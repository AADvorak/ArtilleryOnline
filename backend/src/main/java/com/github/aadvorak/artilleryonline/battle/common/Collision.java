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

    private VelocitiesProjections velocitiesProjections;

    private double interpenetration;

    private double angle;

    private Double sumNormalVelocity;

    private Double impact;

    public double getSumNormalVelocity() {
        if (sumNormalVelocity == null) {
            var first = Math.abs(velocitiesProjections.first().getNormal());
            var second = 0.0;
            if (velocitiesProjections.second() != null) {
                second = Math.abs(velocitiesProjections.second().getNormal());
            }
            sumNormalVelocity = first + second;
        }
        return sumNormalVelocity;
    }

    public double getImpact() {
        if (impact == null) {
            var first = Math.abs(velocitiesProjections.first().getNormal())
                    * pair.first().getModel().getPreCalc().getMass();
            var second = 0.0;
            if (velocitiesProjections.second() != null && pair.second() != null) {
                second = Math.abs(velocitiesProjections.second().getNormal())
                        * pair.second().getModel().getPreCalc().getMass();
            }
            impact = first + second;
        }
        return impact;
    }

    public static Collision withGround(Calculations first, double interpenetration, double angle) {
        return withUnmovable(first, interpenetration, angle, CollideObjectType.GROUND);
    }

    public static Collision withWall(Calculations first, double interpenetration, double angle) {
        return withUnmovable(first, interpenetration, angle, CollideObjectType.WALL);
    }

    public static Collision ofVehicles(Calculations first, Calculations second, double interpenetration) {
        var angle = getCollisionAngle(first.getPosition(), second.getPosition());
        var firstProjections = first.getVelocity().getProjections(angle);
        var secondProjections = second.getVelocity().getProjections(angle);
        return new Collision()
                .setVehicleId(second.getVehicleId())
                .setType(CollideObjectType.VEHICLE)
                .setPair(new CollisionPair(first, second))
                .setVelocitiesProjections(new VelocitiesProjections(firstProjections, secondProjections))
                .setInterpenetration(interpenetration)
                .setAngle(angle);
    }

    public static Collision inverted(Collision collision) {
        return new Collision()
                .setVehicleId(collision.getPair().first().getVehicleId())
                .setType(collision.getType())
                .setPair(new CollisionPair(collision.getPair().second(), collision.getPair().first()))
                .setVelocitiesProjections(
                        new VelocitiesProjections(
                                collision.getVelocitiesProjections().second(),
                                collision.getVelocitiesProjections().first()
                        )
                )
                .setInterpenetration(collision.getInterpenetration())
                .setAngle(collision.getAngle() + Math.PI);
    }

    private static double getCollisionAngle(Position position, Position otherPosition) {
        var sign = Math.signum(otherPosition.getX() - position.getX());
        return Math.asin((position.getY() - otherPosition.getY())
                / position.distanceTo(otherPosition)) + sign * Math.PI / 2;
    }

    private static Collision withUnmovable(Calculations first, double interpenetration,
                                           double angle, CollideObjectType type) {
        return new Collision()
                .setType(type)
                .setPair(new CollisionPair(first, null))
                .setVelocitiesProjections(new VelocitiesProjections(first.getVelocity().getProjections(angle), null))
                .setInterpenetration(interpenetration)
                .setAngle(angle);
    }
}
