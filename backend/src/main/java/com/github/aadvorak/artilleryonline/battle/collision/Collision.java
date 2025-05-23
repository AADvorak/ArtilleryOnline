package com.github.aadvorak.artilleryonline.battle.collision;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.common.VelocitiesProjections;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Collision {

    private static final double RESTITUTION = 0.5;

    private CollideObjectType type;

    private CollisionPair pair;

    private VelocitiesProjections velocitiesProjections;

    private BodyCollisionDataPair bodyCollisionDataPair;

    private Contact contact;

    private Double impact;

    private boolean hit = false;

    public Integer getSecondId() {
        if (pair.second() != null) {
            return pair.second().getId();
        }
        return null;
    }

    public double getClosingVelocity() {
        return firstNormalVelocity() - secondNormalVelocity();
    }

    public double getImpact() {
        if (impact == null) {
            var closingVelocity = getClosingVelocity();
            if  (closingVelocity <= 0.0) {
                impact = 0.0;
            } else if (pair.second() == null) {
                var firstData = bodyCollisionDataPair.first();
                var mass = firstData != null ? firstData.getNormalData().getResultMass() : pair.first().getMass();
                impact = mass * closingVelocity * RESTITUTION;
            } else {
                var firstData = bodyCollisionDataPair.first();
                var secondData = bodyCollisionDataPair.second();
                var firstMass = firstData != null ? firstData.getNormalData().getResultMass() : pair.first().getMass();
                var secondMass = secondData != null ? secondData.getNormalData().getResultMass() : pair.second().getMass();
                impact = firstMass * secondMass * closingVelocity * (1 + RESTITUTION)
                        / (firstMass + secondMass);
            }
        }
        return impact;
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
        BodyCollisionData firstData = null;
        BodyCollisionData secondData = null;
        if (first instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            firstData = BodyCollisionData.of(bodyCalculations.getModel(), contact);
        }
        if (second instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            secondData = BodyCollisionData.of(bodyCalculations.getModel(), contact);
        }
        var firstProjections = first.getVelocity().projections(contact.angle());
        var secondProjections = second.getVelocity().projections(contact.angle());
        return new Collision()
                .setType(type)
                .setPair(new CollisionPair(first, second))
                .setBodyCollisionDataPair(new BodyCollisionDataPair(firstData, secondData))
                .setVelocitiesProjections(new VelocitiesProjections(firstProjections, secondProjections))
                .setContact(contact);
    }

    private double firstNormalVelocity() {
        return bodyCollisionDataPair.first() != null
                ? bodyCollisionDataPair.first().getVelocityProjections().getNormal()
                : velocitiesProjections.first().getNormal();
    }

    private double secondNormalVelocity() {
        if (bodyCollisionDataPair.second() != null) {
            return bodyCollisionDataPair.second().getVelocityProjections().getNormal();
        } else if (velocitiesProjections.second() != null) {
            return velocitiesProjections.second().getNormal();
        }
        return 0;
    }

    private static Collision withUnmovable(
            Calculations<?> first,
            Contact contact,
            CollideObjectType type
    ) {
        BodyCollisionData firstData = null;
        if (first instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            firstData = BodyCollisionData.of(bodyCalculations.getModel(), contact);
        }
        return new Collision()
                .setType(type)
                .setPair(new CollisionPair(first, null))
                .setBodyCollisionDataPair(new BodyCollisionDataPair(firstData, null))
                .setVelocitiesProjections(new VelocitiesProjections(first.getVelocity()
                        .projections(contact.angle()), null))
                .setContact(contact);
    }
}
