package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;

public class CollisionUtils {

    public static void recalculateVelocitiesRigid(Collision collision) {
        var mass = collision.getPair().first().getMass();
        var otherMass = collision.getPair().second().getMass();

        var velocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().first());
        var otherVelocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().second());

        var velocityNormalProjection = velocityProjections.getNormal();
        var otherVelocityNormalProjection = otherVelocityProjections.getNormal();

        velocityProjections.setNormal(getNewVelocityNormalProjection(
                velocityNormalProjection, otherVelocityNormalProjection,
                mass, otherMass));
        collision.getPair().first().setVelocity(velocityProjections.recoverVelocity());

        otherVelocityProjections.setNormal(getNewVelocityNormalProjection(
                otherVelocityNormalProjection, velocityNormalProjection,
                otherMass, mass));
        collision.getPair().second().setVelocity(otherVelocityProjections.recoverVelocity());
    }

    private static double getNewVelocityNormalProjection(
            double velocityNormalProjection, double otherVelocityNormalProjection,
            double mass, double otherMass
    ) {
        return (- Math.abs(mass - otherMass) * velocityNormalProjection
                + 2 * otherMass * otherVelocityNormalProjection) / (mass + otherMass);
    }
}
