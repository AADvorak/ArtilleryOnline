package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.CollisionPair;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;

@Getter
@Setter
@Accessors(chain = true)
public class Collision {

    private CollideObjectType type;

    private CollisionPair pair;

    private VelocitiesProjections velocitiesProjections;

    private Interpenetration interpenetration;

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

    public static Collision ofMissileWithGround(Calculations<MissileModel> first) {
        return new Collision()
                .setPair(new CollisionPair(first, null));
    }

    public static Collision ofShellWithGround(Calculations<ShellModel> first) {
        return new Collision()
                .setPair(new CollisionPair(first, null));
    }

    public static Collision ofVehicleWithGround(Calculations<VehicleModel> first, Interpenetration interpenetration) {
        return ofVehicleWithUnmovable(first, interpenetration, CollideObjectType.GROUND);
    }

    public static Collision ofVehicleWithWall(Calculations<VehicleModel> first, Interpenetration interpenetration) {
        return ofVehicleWithUnmovable(first, interpenetration, CollideObjectType.WALL);
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
                .setInterpenetration(
                        Optional.ofNullable(collision.getInterpenetration())
                                .map(Interpenetration::inverted)
                        .orElse(null)
                );
    }

    public static Collision withVehicle(Calculations<?> first, Calculations<VehicleModel> second,
                                         Interpenetration interpenetration) {
        var firstProjections = first.getVelocity().projections(interpenetration.angle());
        var secondProjections = second.getVelocity().projections(interpenetration.angle());
        return new Collision()
                .setType(CollideObjectType.VEHICLE)
                .setPair(new CollisionPair(first, second))
                .setVelocitiesProjections(new VelocitiesProjections(firstProjections, secondProjections))
                .setInterpenetration(interpenetration);
    }

    private static Collision ofVehicleWithUnmovable(
            Calculations<VehicleModel> first,
            Interpenetration interpenetration,
            CollideObjectType type
    ) {
        return new Collision()
                .setType(type)
                .setPair(new CollisionPair(first, null))
                .setVelocitiesProjections(new VelocitiesProjections(first.getVelocity()
                        .projections(interpenetration.angle()), null))
                .setInterpenetration(interpenetration);
    }
}
