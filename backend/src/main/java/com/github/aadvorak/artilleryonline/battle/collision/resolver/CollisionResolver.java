package com.github.aadvorak.artilleryonline.battle.collision.resolver;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.BodyCollisionData;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;

public class CollisionResolver {

    private static final double RESTITUTION = 0.5;

    private static final double FRICTION_VELOCITY_THRESHOLD = 0.1;

    private static final boolean LOGGING = true;

    public void resolve(Collision collision, double timeStepSecs) {
        BodyModel<?, ?, ?, ?> firstModel = null;
        BodyModel<?, ?, ?, ?> secondModel = null;
        BodyCollisionData firstData = collision.getBodyCollisionDataPair().first();
        BodyCollisionData secondData = collision.getBodyCollisionDataPair().second();
        if (collision.getPair().first() != null
                && collision.getPair().first() instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            firstModel = bodyCalculations.getModel();
        }
        if (collision.getPair().second() != null
                && collision.getPair().second() instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            secondModel = bodyCalculations.getModel();
        }
        if (firstModel == null) {
            return;
        }
        if (LOGGING) {
            if (secondModel != null) {
                System.out.printf("Collision of bodies ids = [%d, %d]\n%s\n",
                        collision.getPair().first().getId(), collision.getPair().second().getId(),
                        collision.getContact());
            } else {
                System.out.printf("Collision with unmovable of body id = %d\n%s\n%s\n",
                        collision.getPair().first().getId(), collision.getContact(), firstData);
            }
        }
        var closingVelocity = collision.getClosingVelocity();
        if (closingVelocity > 0) {
            resolveClosingVelocity(collision, closingVelocity, firstModel, secondModel, firstData, secondData);
        }
        var frictionVelocity = -firstData.getVelocityProjections().getTangential();
        if (secondData != null) {
            frictionVelocity += secondData.getVelocityProjections().getTangential();
        }
        if (Math.abs(frictionVelocity) > FRICTION_VELOCITY_THRESHOLD) {
            resolveFrictionVelocity(collision, frictionVelocity, firstModel, secondModel, firstData, secondData);
        }
        recalculatePositionsAndResolveInterpenetration(collision, timeStepSecs);
        if (LOGGING) System.out.print("----------------End collision resolution------------------\n");
    }

    private void resolveClosingVelocity(
            Collision collision, double closingVelocity,
            BodyModel<?, ?, ?, ?> firstModel,
            BodyModel<?, ?, ?, ?> secondModel,
            BodyCollisionData firstData,
            BodyCollisionData secondData
    ) {
        if (LOGGING) System.out.print("Closing resolving\n");
        if (secondData == null) {
            var impulseDelta = firstData.getNormalData().getResultMass() * closingVelocity * RESTITUTION;
            recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                    firstData.getNormalData(), impulseDelta, 1);
        } else {
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
            if (LOGGING) System.out.print("Friction resolving\n");
            var impulseDelta = -firstData.getTangentialData().getResultMass() * frictionVelocity
                    * collision.getContact().depth() * 10.0;
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

    private void recalculatePositionsAndResolveInterpenetration(Collision collision, double timeStepSecs) {
        var object = collision.getPair().first();
        var otherObject = collision.getPair().second();
        object.calculateNextPosition(timeStepSecs);
        if (otherObject == null) {
            object.applyNormalMoveToNextPosition(-collision.getContact().depth(),
                    collision.getContact().angle());
        } else {
            otherObject.calculateNextPosition(timeStepSecs);
            var mass = object.getMass();
            var otherMass = otherObject.getMass();
            var normalMovePerMass = collision.getContact().depth() / (mass + otherMass);
            var normalMove = normalMovePerMass * otherMass;
            var otherNormalMove = normalMovePerMass * mass;
            object.applyNormalMoveToNextPosition(- normalMove, collision.getContact().angle());
            otherObject.applyNormalMoveToNextPosition(otherNormalMove, collision.getContact().angle());
            // todo remove from here
            otherObject.getCollisions().add(collision.inverted());
        }
    }
}
