package com.github.aadvorak.artilleryonline.battle.collision.resolver;

import com.github.aadvorak.artilleryonline.battle.calculations.BodyCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.BodyCollisionData;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import org.springframework.stereotype.Component;

@Component
public class CollisionResolver {

    private static final double RESTITUTION = 0.5;

    private static final double FRICTION_VELOCITY_THRESHOLD = 0.1;

    private final boolean logging;

    public CollisionResolver(ApplicationSettings settings) {
        logging = settings.isDebug();
    }

    public void resolve(Collision collision, double timeStepSecs) {
        var first = collision.getPair().first();
        var second = collision.getPair().second();
        BodyModel<?, ?, ?, ?> firstModel = null;
        BodyModel<?, ?, ?, ?> secondModel = null;
        BodyCollisionData firstData = collision.getBodyCollisionDataPair().first();
        BodyCollisionData secondData = collision.getBodyCollisionDataPair().second();
        if (first instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            firstModel = bodyCalculations.getModel();
        }
        if (second instanceof BodyCalculations<?, ?, ?, ?, ?> bodyCalculations) {
            secondModel = bodyCalculations.getModel();
        }

        if (collision.isHit()) {
            if (secondModel != null) {
                var hitDirection = first.getVelocity().normalized();
                var hitData = BodyCollisionData.getComponentData(secondModel, hitDirection,
                        collision.getContact().position());
                var impulseDelta = first.getMass() * first.getVelocity().magnitude();
                System.out.printf("Object id %d = hits object id = %d]\n%s\nHitData: %s\n",
                        collision.getPair().first().getId(), collision.getPair().second().getId(),
                        collision.getContact(), hitData);
                recalculateBodyVelocity(secondModel.getState().getVelocity(), collision.getContact(),
                        hitData, impulseDelta, -1, false);
                if (logging) System.out.print("----------------End hit resolution------------------\n");
            }
            return;
        }

        if (logging) {
            if (secondModel != null) {
                System.out.printf("Collision of objects ids = [%d, %d]\n%s\n",
                        collision.getPair().first().getId(), collision.getPair().second().getId(),
                        collision.getContact());
            } else {
                System.out.printf("Collision with unmovable of object id = %d\n%s\n%s\n",
                        collision.getPair().first().getId(), collision.getContact(), firstData);
            }
        }

        var closingVelocity = collision.getClosingVelocity();
        if (closingVelocity > 0) {
            if (logging) System.out.print("Closing resolving\n");
            if (second == null) {
                var mass = firstData != null ? firstData.getNormalData().getResultMass() : first.getMass();
                var impulseDelta = mass * closingVelocity * RESTITUTION;
                if (firstModel != null && firstData != null) {
                    recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                            firstData.getNormalData(), impulseDelta, 1, false);
                } else {
                    recalculatePointVelocity(first.getVelocity(), collision.getContact(), first.getMass(), impulseDelta);
                }
            } else {
                var firstMass = firstData != null ? firstData.getNormalData().getResultMass() : first.getMass();
                var secondMass = secondData != null ? secondData.getNormalData().getResultMass() : second.getMass();
                var impulseDelta = firstMass * secondMass * closingVelocity * (1 + RESTITUTION)
                        / (firstMass + secondMass);
                if (logging) System.out.printf("First object:\n%s\n", firstData);
                if (firstModel != null && firstData != null) {
                    recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                            firstData.getNormalData(), impulseDelta, 1, false);
                } else {
                    recalculatePointVelocity(first.getVelocity(), collision.getContact(), first.getMass(), impulseDelta);
                }
                if (logging) System.out.printf("Second object:\n%s\n", secondData);
                if (secondModel != null && secondData != null) {
                    recalculateBodyVelocity(secondModel.getState().getVelocity(), collision.getContact(),
                            secondData.getNormalData(), impulseDelta, -1, false);
                } else {
                    recalculatePointVelocity(second.getVelocity(), collision.getContact(), second.getMass(), impulseDelta);
                }
            }
        }

        var frictionVelocity = 0.0;
        if (firstData != null) {
            frictionVelocity += firstData.getVelocityProjections().getTangential();
        }
        if (secondData != null) {
            frictionVelocity += secondData.getVelocityProjections().getTangential();
        }
        if (Math.abs(frictionVelocity) > FRICTION_VELOCITY_THRESHOLD) {
            resolveFrictionVelocity(collision, frictionVelocity, firstModel, secondModel, firstData, secondData);
        }

        recalculatePositionsAndResolveInterpenetration(collision, timeStepSecs);
        if (logging) System.out.print("----------------End collision resolution------------------\n");
    }

    private void resolveFrictionVelocity(
            Collision collision, double frictionVelocity,
            BodyModel<?, ?, ?, ?> firstModel,
            BodyModel<?, ?, ?, ?> secondModel,
            BodyCollisionData firstData,
            BodyCollisionData secondData
    ) {
        if (secondData == null) {
            if (logging) System.out.print("Friction resolving\n");
            var depth = Math.min(collision.getContact().depth(), 0.04);
            var impulseDelta = frictionVelocity * depth * getFrictionCoefficient(collision.getPair().first());
            recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                    firstData.getTangentialData(), impulseDelta, 1, true);
        }
    }

    private void recalculateBodyVelocity(
            BodyVelocity velocity,
            Contact contact,
            BodyCollisionData.ComponentData componentData,
            double impulseDelta,
            int sign,
            boolean tangential
    ) {
        var velocityDelta = - sign * impulseDelta / componentData.getResultMass();
        var imc = componentData.getInertiaToMassCoefficient();
        if (logging) System.out.printf("velocityDelta = %.3f\n", velocityDelta);
        if (componentData.getInertiaToMassCoefficient() > 0) {
            var angleVelocityDelta = componentData.getRotationSign() * imc * velocityDelta
                    / componentData.getDistanceToAxis() / (1 + imc);
            if (logging) System.out.printf("angleVelocityDelta = %.3f\n", angleVelocityDelta);
            velocity.setAngle(velocity.getAngle() + angleVelocityDelta);
        }
        if (componentData.getInertiaToMassCoefficient() < 1) {
            var movingVelocityDeltaMagnitude = velocityDelta / (1 + imc);
            var movingVelocityDelta = new VectorProjections(contact.angle())
                    .setNormal(tangential ? 0.0 : movingVelocityDeltaMagnitude)
                    .setTangential(tangential ? movingVelocityDeltaMagnitude : 0.0)
                    .recoverVelocity();
            if (logging) System.out.printf("movingVelocityDelta = %s\n", movingVelocityDelta);
            velocity
                    .setX(velocity.getX() + movingVelocityDelta.getX())
                    .setY(velocity.getY() + movingVelocityDelta.getY());
        }
    }

    private void recalculatePointVelocity(
            Velocity velocity,
            Contact contact,
            double mass,
            double impulseDelta
    ) {
        var velocityDelta = - impulseDelta / mass;
        if (logging) System.out.printf("velocityDelta = %.3f\n", velocityDelta);
        var movingVelocityDelta = new VectorProjections(contact.angle())
                .setNormal(velocityDelta)
                .recoverVelocity();
        if (logging) System.out.printf("movingVelocityDelta = %s\n", movingVelocityDelta);
        velocity
                .setX(velocity.getX() + movingVelocityDelta.getX())
                .setY(velocity.getY() + movingVelocityDelta.getY());
    }

    private void recalculatePositionsAndResolveInterpenetration(Collision collision, double timeStepSecs) {
        var object = collision.getPair().first();
        var otherObject = collision.getPair().second();
        object.calculateNextPosition(timeStepSecs);
        var depth = Math.min(collision.getContact().depth(), 0.1);
        if (otherObject == null) {
            object.applyNormalMoveToNextPosition(-depth, collision.getContact().angle());
        } else {
            otherObject.calculateNextPosition(timeStepSecs);
            var mass = object.getMass();
            var otherMass = otherObject.getMass();
            var normalMovePerMass = depth / (mass + otherMass);
            var normalMove = normalMovePerMass * otherMass;
            var otherNormalMove = normalMovePerMass * mass;
            object.applyNormalMoveToNextPosition(- normalMove, collision.getContact().angle());
            otherObject.applyNormalMoveToNextPosition(otherNormalMove, collision.getContact().angle());
        }
    }

    private double getFrictionCoefficient(Calculations<?> calculations) {
        if (calculations instanceof VehicleCalculations) {
            return 25.0;
        }
        return 0.1;
    }
}
