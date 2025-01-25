package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.CollisionPair;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Collision {

    private CollideObjectType type;

    private CollisionPair pair;

    private VelocitiesProjections velocitiesProjections;

    private double interpenetration;

    private double angle;

    private Double sumNormalVelocity;

    private Double impact;

    public Integer getVehicleId() {
        if (pair.second() != null) {
            return pair.second().getId();
        }
        return null;
    }

    public double getSumNormalVelocity() {
        if (sumNormalVelocity == null) {
            var first = 0.0;
            if (velocitiesProjections.first() != null) {
                first = Math.abs(velocitiesProjections.first().getNormal());
            }
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
            var first = 0.0;
            if (velocitiesProjections.first() != null && pair.first() != null) {
                first = Math.abs(velocitiesProjections.first().getNormal())
                        * pair.first().getMass();
            }
            var second = 0.0;
            if (velocitiesProjections.second() != null && pair.second() != null) {
                second = Math.abs(velocitiesProjections.second().getNormal())
                        * pair.second().getMass();
            }
            impact = first + second;
        }
        return impact;
    }

    public static Collision ofShellWithGround(Calculations<ShellModel> first) {
        return new Collision()
                .setPair(new CollisionPair(first, null));
    }

    public static Collision ofShellWithVehicle(Calculations<ShellModel> first, Calculations<VehicleModel> second) {
        return withVehicle(first, second);
    }

    public static Collision ofVehicleWithGround(Calculations<VehicleModel> first, double interpenetration, double angle) {
        return ofVehicleWithUnmovable(first, interpenetration, angle, CollideObjectType.GROUND);
    }

    public static Collision ofVehicleWithWall(Calculations<VehicleModel> first, double interpenetration, double angle) {
        return ofVehicleWithUnmovable(first, interpenetration, angle, CollideObjectType.WALL);
    }

    public static Collision ofTwoVehicles(Calculations<VehicleModel> first, Calculations<VehicleModel> second, double interpenetration) {
        return withVehicle(first, second)
                .setInterpenetration(interpenetration);
    }

    public static Collision inverted(Collision collision) {
        return new Collision()
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

    private static Collision withVehicle(Calculations<?> first, Calculations<VehicleModel> second) {
        var angle = getCollisionAngle(first.getPosition(), second.getPosition());
        var firstProjections = first.getVelocity().getProjections(angle);
        var secondProjections = second.getVelocity().getProjections(angle);
        return new Collision()
                .setType(CollideObjectType.VEHICLE)
                .setPair(new CollisionPair(first, second))
                .setVelocitiesProjections(new VelocitiesProjections(firstProjections, secondProjections))
                .setAngle(angle);
    }

    private static double getCollisionAngle(Position position, Position otherPosition) {
        var dx = otherPosition.getX() - position.getX();
        var dy = otherPosition.getY() - position.getY();
        return Math.atan2(dy, dx) + Math.PI / 2;
    }

    private static Collision ofVehicleWithUnmovable(Calculations<VehicleModel> first, double interpenetration,
                                                    double angle, CollideObjectType type) {
        return new Collision()
                .setType(type)
                .setPair(new CollisionPair(first, null))
                .setVelocitiesProjections(new VelocitiesProjections(first.getVelocity().getProjections(angle), null))
                .setInterpenetration(interpenetration)
                .setAngle(angle);
    }
}
