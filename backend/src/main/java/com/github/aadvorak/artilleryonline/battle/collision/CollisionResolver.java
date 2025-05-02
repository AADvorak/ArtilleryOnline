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
        BodyModel<?, ?, ?, ?> firstModel = null;
        BodyModel<?, ?, ?, ?> secondModel = null;
        BodyCollisionData firstData = null;
        BodyCollisionData secondData = null;
        if (collision.getPair().first() != null
                && collision.getPair().first() instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            firstModel = bodyCalculations.getModel();
            firstData = BodyCollisionData.of(bodyCalculations.getModel(), collision.getContact());
        }
        if (collision.getPair().second() != null
                && collision.getPair().second() instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            secondModel = bodyCalculations.getModel();
            secondData = BodyCollisionData.of(bodyCalculations.getModel(), collision.getContact());
        }
        if (firstModel == null) {
            return;
        }
        var closingVelocity = firstData.getVelocityProjections().getNormal();
        if (secondData != null) {
            closingVelocity -= secondData.getVelocityProjections().getNormal();
        }
        if (closingVelocity > 0) {
            resolveClosingVelocity(collision, closingVelocity, firstModel, secondModel, firstData, secondData);
        }
//        var frictionVelocity = -firstData.getVelocityProjections().getTangential();
//        if (secondData != null) {
//            frictionVelocity += secondData.getVelocityProjections().getTangential();
//        }
//        resolveFrictionVelocity(collision, frictionVelocity, firstModel, secondModel, firstData, secondData);
        if (LOGGING) System.out.print("----------------End collision resolution------------------\n");
    }

    private void resolveClosingVelocity(
            Collision collision, double closingVelocity,
            BodyModel<?, ?, ?, ?> firstModel,
            BodyModel<?, ?, ?, ?> secondModel,
            BodyCollisionData firstData,
            BodyCollisionData secondData
    ) {
        if (secondData == null) {
            if (LOGGING) System.out.printf("Collision with unmovable of body id = %d\n%s\n%s\n",
                    collision.getPair().first().getId(), collision.getContact(), firstData);
            var impulseDelta = firstData.getNormalData().getResultMass() * closingVelocity * RESTITUTION;
            recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                    firstData.getNormalData(), impulseDelta, 1);
        } else {
            if (LOGGING) System.out.printf("Collision of bodies ids = [%d, %d]\n%s\n",
                    collision.getPair().first().getId(), collision.getPair().second().getId(), collision.getContact());
            var impulseDelta = firstData.getNormalData().getResultMass() * secondData.getNormalData().getResultMass()
                    * closingVelocity * (1 + RESTITUTION)
                    / (secondData.getNormalData().getResultMass() + firstData.getNormalData().getResultMass());
            if (LOGGING) System.out.printf("First body:\n%s\n", firstData);
            recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                    firstData.getNormalData(), impulseDelta, 1);
            if (LOGGING) System.out.printf("Second body:\n%s\n", secondData);
            recalculateBodyVelocity(secondModel.getState().getVelocity(), collision.getContact(),
                    secondData.getNormalData(), impulseDelta, -1);
        }
    }

    private void resolveFrictionVelocity(
            Collision collision, double frictionVelocity,
            BodyModel<?, ?, ?, ?> firstModel,
            BodyModel<?, ?, ?, ?> secondModel,
            BodyCollisionData firstData,
            BodyCollisionData secondData
    ) {
        if (secondData == null) {
            if (LOGGING) System.out.printf("Friction with unmovable of body id = %d\n%s\n%s\n",
                    collision.getPair().first().getId(), collision.getContact(), firstData);
            var impulseDelta = -firstData.getTangentialData().getResultMass() * frictionVelocity * 0.3;
            recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                    firstData.getTangentialData(), impulseDelta, 1);
        }
    }

    private void recalculateBodyVelocity(
            BodyVelocity velocity,
            Contact contact,
            BodyCollisionData.ComponentData componentData,
            double impulseDelta,
            int sign
    ) {
        var velocityDelta = - sign * impulseDelta / componentData.getResultMass();
        if (LOGGING) System.out.printf("velocityDelta = %.3f\n", velocityDelta);
        if (componentData.getInertiaToMassCoefficient() > 0) {
            var angleVelocityDelta = componentData.getRotationSign() * componentData.getInertiaToMassCoefficient()
                    * velocityDelta / componentData.getDistanceToAxis();
            if (LOGGING) System.out.printf("angleVelocityDelta = %.3f\n", angleVelocityDelta);
            velocity.setAngle(velocity.getAngle() + angleVelocityDelta);
        }
        if (componentData.getInertiaToMassCoefficient() < 1) {
            var movingVelocityDeltaMagnitude = (1 - componentData.getInertiaToMassCoefficient()) * velocityDelta;
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
