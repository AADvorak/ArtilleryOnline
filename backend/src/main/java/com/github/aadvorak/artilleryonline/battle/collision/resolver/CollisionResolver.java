package com.github.aadvorak.artilleryonline.battle.collision.resolver;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.collision.BodyCollisionData;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import org.springframework.stereotype.Component;

@Component
public class CollisionResolver {

    private static final double FRICTION_VELOCITY_THRESHOLD = 0.1;

    private final boolean logging;

    public CollisionResolver(ApplicationSettings settings) {
        logging = settings.isDebug();
    }

    public void resolve(Collision collision, BattleModel battleModel) {
        var kineticEnergyBefore = collision.getSumKineticEnergy();
        if (logging) System.out.print("----------------Begin collision resolution------------------\n");
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

        logEnergyVelocityAndPosition("before", kineticEnergyBefore, firstModel, secondModel);

        if (collision.isHit()) {
            if (secondModel != null) {
                var hitDirection = first.getVelocity().normalized();
                var hitData = BodyCollisionData.getComponentData(secondModel, hitDirection,
                        collision.getContact().position());
                var recalcMass = first.getMass() * hitData.getResultMass() / second.getMass();
                var impulseDelta = recalcMass * first.getVelocity().magnitude();
                if (logging) System.out.printf("Object %s hits object %s\n%s\nHitData: %s\n",
                        getObjectDescription(first),
                        getObjectDescription(second),
                        collision.getContact(), hitData);
                if (logging) System.out.printf("Mass = %.6f, recalcMass = %.6f\n", first.getMass(), recalcMass);
                recalculateBodyVelocity(secondModel.getState().getVelocity(), collision.getContact(),
                        hitData, impulseDelta, -1, false);
                var kineticEnergyAfter = second.getKineticEnergy();
                if (kineticEnergyAfter > kineticEnergyBefore) {
                    var velocityMultiplier = Math.sqrt(kineticEnergyBefore / kineticEnergyAfter);
                    multiplyBodyVelocity(secondModel.getState().getVelocity(), velocityMultiplier);
                }
                logEnergyVelocityAndPosition("after", kineticEnergyAfter, firstModel, secondModel);
                if (logging) System.out.print("----------------End hit resolution------------------\n");
            }
            return;
        }

        if (logging) {
            if (second != null) {
                System.out.printf("Collision of objects [%s, %s]\n%s\n",
                        getObjectDescription(first),
                        getObjectDescription(second),
                        collision.getContact());
            } else {
                System.out.printf("Collision with unmovable of object %s\n%s\n",
                        getObjectDescription(first), collision.getContact());
            }
        }

        if (collision.getImpact() > 0) {
            if (logging) System.out.print("Closing resolving\n");
            if (logging) System.out.printf("First object:\n%s\n", firstData);
            if (firstModel != null && firstData != null) {
                recalculateBodyVelocity(firstModel.getState().getVelocity(), collision.getContact(),
                        firstData.getNormalData(), collision.getImpact(), 1, false);
            } else {
                recalculatePointVelocity(first.getVelocity(), collision.getContact(),
                        first.getMass(), collision.getImpact());
            }
            if (second != null) {
                if (logging) System.out.printf("Second object:\n%s\n", secondData);
                if (secondModel != null && secondData != null) {
                    recalculateBodyVelocity(secondModel.getState().getVelocity(), collision.getContact(),
                            secondData.getNormalData(), collision.getImpact(), -1, false);
                } else {
                    recalculatePointVelocity(second.getVelocity(), collision.getContact(),
                            second.getMass(), collision.getImpact());
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
            resolveFrictionVelocity(collision, frictionVelocity, firstModel, secondModel, firstData, secondData,
                    battleModel.getRoom().getSpecs().getGroundMaxDepth());
        }

        recalculatePositionsAndResolveInterpenetration(collision, battleModel.getCurrentTimeStepSecs());

        var kineticEnergyAfter = collision.getSumKineticEnergy();
        if (kineticEnergyAfter > kineticEnergyBefore) {
            var velocityMultiplier = Math.sqrt(kineticEnergyBefore / kineticEnergyAfter);
            if (firstModel != null) {
                multiplyBodyVelocity(firstModel.getState().getVelocity(), velocityMultiplier);
            } else {
                multiplyVelocity(first.getVelocity(), velocityMultiplier);
            }
            if (second != null) {
                if (secondModel != null) {
                    multiplyBodyVelocity(secondModel.getState().getVelocity(), velocityMultiplier);
                } else {
                    multiplyVelocity(second.getVelocity(), velocityMultiplier);
                }
            }
        }

        logEnergyVelocityAndPosition("after", kineticEnergyAfter, firstModel, secondModel);
        if (logging) System.out.print("----------------End collision resolution------------------\n");
    }

    private void resolveFrictionVelocity(
            Collision collision, double frictionVelocity,
            BodyModel<?, ?, ?, ?> firstModel,
            BodyModel<?, ?, ?, ?> secondModel,
            BodyCollisionData firstData,
            BodyCollisionData secondData,
            double groundMaxDepth
    ) {
        if (secondData == null) {
            if (logging) System.out.print("Friction resolving\n");
            var depth = Math.min(collision.getContact().depth(), groundMaxDepth);
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
        if (logging) System.out.printf("velocityDelta = %.6f\n", velocityDelta);
        if (componentData.getInertiaToMassCoefficient() > 0) {
            var angleVelocityDelta = componentData.getRotationSign() * imc * velocityDelta
                    / componentData.getDistanceToAxis() / (1 + imc);
            if (logging) System.out.printf("angleVelocityDelta = %.6f\n", angleVelocityDelta);
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
        if (logging) System.out.printf("velocityDelta = %.6f\n", velocityDelta);
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
        var moveMagnitude = Math.min(collision.getContact().depth(), 0.1);
        if (otherObject == null) {
            object.applyNormalMoveToNextPosition(-moveMagnitude, collision.getContact().angle());
            if (object.getModel() instanceof BodyModel<?,?,?,?> bodyModel) {
                bodyModel.getState().applyNormalMoveToPosition(-moveMagnitude, collision.getContact().angle());
            }
            if (logging) System.out.printf("First object normal move = %s\n", -moveMagnitude);
        } else {
            otherObject.calculateNextPosition(timeStepSecs);
            var mass = object.getMass();
            var otherMass = otherObject.getMass();
            var normalMovePerMass = moveMagnitude / (mass + otherMass);
            var normalMove = normalMovePerMass * otherMass;
            var otherNormalMove = normalMovePerMass * mass;
            object.applyNormalMoveToNextPosition(- normalMove, collision.getContact().angle());
            otherObject.applyNormalMoveToNextPosition(otherNormalMove, collision.getContact().angle());
            if (object.getModel() instanceof BodyModel<?,?,?,?> bodyModel) {
                bodyModel.getState().applyNormalMoveToPosition(- normalMove, collision.getContact().angle());
            }
            if (otherObject.getModel() instanceof BodyModel<?,?,?,?> otherBodyModel) {
                otherBodyModel.getState().applyNormalMoveToPosition(otherNormalMove, collision.getContact().angle());
            }
            if (logging) {
                System.out.printf("First object normal move = %s\n", -normalMove);
                System.out.printf("Second object normal move = %s\n", otherNormalMove);
            }
        }
    }

    private void multiplyBodyVelocity(BodyVelocity velocity, double multiplier) {
        velocity.setX(velocity.getX() * multiplier);
        velocity.setY(velocity.getY() * multiplier);
        velocity.setAngle(velocity.getAngle() * multiplier);
    }

    private void multiplyVelocity(Velocity velocity, double multiplier) {
        velocity.setX(velocity.getX() * multiplier);
        velocity.setY(velocity.getY() * multiplier);
    }

    private double getFrictionCoefficient(Calculations<?> calculations) {
        if (calculations instanceof VehicleCalculations) {
            return 25.0;
        }
        return 0.1;
    }

    private void logEnergyVelocityAndPosition(
            String prefix,
            double energy,
            BodyModel<?,?,?,?> firstModel,
            BodyModel<?,?,?,?> secondModel
    ) {
        if (logging) {
            System.out.printf("%s collision resolution energy = %.6f\n", prefix, energy);
            if (firstModel != null) {
                System.out.printf("first velocity = %s, position = %s\n",
                        firstModel.getState().getVelocity(),
                        firstModel.getState().getPosition());
            }
            if (secondModel != null) {
                System.out.printf("second velocity = %s, position = %s\n",
                        secondModel.getState().getVelocity(),
                        secondModel.getState().getPosition());
            }
        }
    }

    private String getObjectDescription(Calculations<?> calculations) {
        var description = calculations.getId().toString();
        switch (calculations) {
            case VehicleCalculations vehicle -> description += " (vehicle hull)";
            case WheelCalculations wheel -> {
                if (wheel.getSign().equals(WheelSign.LEFT)) {
                    description += " (vehicle left wheel)";
                } else if (wheel.getSign().equals(WheelSign.RIGHT)) {
                    description += " (vehicle right wheel)";
                }
            }
            case BoxCalculations box -> description += " (box)";
            case DroneCalculations drone -> description += " (drone)";
            default -> {
            }
        }
        return description;
    }
}
