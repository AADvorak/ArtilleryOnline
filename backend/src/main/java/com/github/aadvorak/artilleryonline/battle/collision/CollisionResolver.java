package com.github.aadvorak.artilleryonline.battle.collision;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;

public class CollisionResolver {

    private static final double RESTITUTION = 0.5;

    private static final boolean LOGGING = true;

    public void resolve(Collision collision) {
        BodyModel<?,?,?,?> firstModel = null;
        BodyModel<?,?,?,?> secondModel = null;
        BodyCollisionData firstData = null;
        BodyCollisionData secondData = null;
        if (collision.getPair().first() != null
                && collision.getPair().first() instanceof BodyCalculations<?,?,?,?,?> bodyCalculations) {
            firstModel = bodyCalculations.getModel();
            firstData = BodyCollisionData.of(bodyCalculations.getModel(), collision.getContact());
        }
        if (collision.getPair().second() != null
                && collision.getPair().second() instanceof BodyCalculations<?,?,?,?,?> bodyCalculations) {
            secondModel = bodyCalculations.getModel();
            secondData = BodyCollisionData.of(bodyCalculations.getModel(), collision.getContact());
        }
        if (firstModel == null) {
            return;
        }
        var closingVelocity = -firstData.getVelocityProjections().getNormal();
        if (secondData != null) {
            closingVelocity += secondData.getVelocityProjections().getNormal();
        }
        if (closingVelocity <= 0) {
            return;
        }
        if (secondData == null) {
            if (LOGGING) System.out.printf("Collision with unmovable of body id = %d\n", collision.getPair().first().getId());
            var impulseDelta = firstData.getResultMass() * closingVelocity * (1 + RESTITUTION);
            recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                    firstData, impulseDelta, 1);
        } else {
            if (LOGGING) System.out.printf("Collision of bodies ids = [%d, %d]\n",
                    collision.getPair().first().getId(), collision.getPair().second().getId());
            var impulseDelta = firstData.getResultMass() * secondData.getResultMass() * closingVelocity
                    * (1 + RESTITUTION) / (secondData.getResultMass() + firstData.getResultMass());
            if (LOGGING) System.out.printf("First body: \n");
            recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                    firstData, impulseDelta, 1);
            if (LOGGING) System.out.printf("Second body: \n");
            recalculateBodyVelocity(secondModel.getState().getVelocity(), collision.getContact(),
                    secondData, impulseDelta, -1);
        }
        if (LOGGING) System.out.printf("----------------End collision resolution------------------\n");
    }

    private void recalculateBodyVelocity(
            BodyVelocity velocity,
            Contact contact,
            BodyCollisionData collisionData,
            double impulseDelta,
            int sign
    ) {
        var velocityDelta = sign * impulseDelta / collisionData.getResultMass();
        if (LOGGING) System.out.printf("%s\n%s\nvelocityDelta = %.3f\n", contact, collisionData, velocityDelta);
        if (collisionData.getInertiaToMassCoefficient() > 0) {
            var angleVelocityDelta = collisionData.getRotationSign() * collisionData.getInertiaToMassCoefficient()
                    * velocityDelta / collisionData.getDistanceToAxis();
            if (LOGGING) System.out.printf("angleVelocityDelta = %.3f\n", angleVelocityDelta);
            velocity.setAngle(velocity.getAngle() + angleVelocityDelta);
        }
        if (collisionData.getInertiaToMassCoefficient() < 1) {
            var movingVelocityDeltaMagnitude = (1 - collisionData.getInertiaToMassCoefficient()) * velocityDelta;
            var movingVelocityDelta = new VectorProjections(contact.angle())
                    .setNormal(movingVelocityDeltaMagnitude)
                    .recoverVelocity();
            if (LOGGING) System.out.printf("movingVelocityDelta = %s\n", movingVelocityDelta);
            velocity
                    .setX(velocity.getX() + movingVelocityDelta.getX())
                    .setY(velocity.getY() + movingVelocityDelta.getY());
        }
    }
}
