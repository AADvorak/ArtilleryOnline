package com.github.aadvorak.artilleryonline.battle.common;

import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.CollisionPair;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
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

    private Contact contact;

    private Double sumNormalVelocity;

    private Double impact;

    public Integer getSecondId() {
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

    public Collision inverted() {
        return new Collision()
                .setType(type)
                .setPair(new CollisionPair(pair.second(), pair.first()))
                .setVelocitiesProjections(
                        new VelocitiesProjections(
                                velocitiesProjections.second(),
                                velocitiesProjections.first()
                        )
                )
                .setContact(
                        Optional.ofNullable(contact)
                                .map(Contact::inverted)
                                .orElse(null)
                );
    }

    public boolean isRicochet() {
        if (pair.first() instanceof ShellCalculations shell) {
            var shellType = shell.getModel().getSpecs().getType();
            if (!ShellType.AP.equals(shellType)) {
                return false;
            }
            if (pair.second() instanceof WheelCalculations) {
                return false;
            }
            var projections = velocitiesProjections.first();
            var normalTangentialRatio = Math.abs(projections.getNormal() / projections.getTangential());
            return normalTangentialRatio < 0.4;
        }
        return false;
    }

    public static Collision ofMissileWithGround(Calculations<MissileModel> first) {
        return new Collision()
                .setPair(new CollisionPair(first, null));
    }

    public static Collision ofShellWithGround(Calculations<ShellModel> first) {
        return new Collision()
                .setPair(new CollisionPair(first, null));
    }

    public static Collision withGround(Calculations<?> first, Contact contact) {
        return withUnmovable(first, contact, CollideObjectType.GROUND);
    }

    public static Collision withWall(Calculations<?> first, Contact contact) {
        return withUnmovable(first, contact, CollideObjectType.WALL);
    }

    public static Collision withSurface(Calculations<?> first, Contact contact) {
        return withUnmovable(first, contact, CollideObjectType.SURFACE);
    }

    public static Collision withVehicle(Calculations<?> first, Calculations<VehicleModel> second,
                                         Contact contact) {
        return withMovable(first, second, contact, CollideObjectType.VEHICLE);
    }

    public static Collision withMissile(Calculations<?> first, Calculations<MissileModel> second,
                                        Contact contact) {
        return withMovable(first, second, contact, CollideObjectType.MISSILE);
    }

    public static Collision withDrone(Calculations<?> first, Calculations<DroneModel> second,
                                        Contact contact) {
        return withMovable(first, second, contact, CollideObjectType.DRONE);
    }

    public static Collision withMovable(Calculations<?> first, Calculations<?> second,
                                        Contact contact, CollideObjectType type) {
        var firstProjections = first.getVelocity().projections(contact.angle());
        var secondProjections = second.getVelocity().projections(contact.angle());
        return new Collision()
                .setType(type)
                .setPair(new CollisionPair(first, second))
                .setVelocitiesProjections(new VelocitiesProjections(firstProjections, secondProjections))
                .setContact(contact);
    }

    private static Collision withUnmovable(
            Calculations<?> first,
            Contact contact,
            CollideObjectType type
    ) {
        return new Collision()
                .setType(type)
                .setPair(new CollisionPair(first, null))
                .setVelocitiesProjections(new VelocitiesProjections(first.getVelocity()
                        .projections(contact.angle()), null))
                .setContact(contact);
    }
}
