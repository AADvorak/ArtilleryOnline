package com.github.aadvorak.artilleryonline.battle.collision;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.resolver.CollisionResolver;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CollisionsProcessor {

    private final ApplicationSettings settings;

    private final Set<CollisionsDetector> detectors;

    private final Set<CollisionPreprocessor> preprocessors;

    private final Set<CollisionPostprocessor> postprocessors;

    private final CollisionResolver resolver;

    public void process(BattleCalculations battle) {
        process(battle, 1);
        postprocessCollisions(battle);
    }

    public void process(BattleCalculations battle, int iterationNumber) {
        detectAllCollisions(battle, iterationNumber);
        resolveStrongestCollisions(battle);
        if (collisionsExist(battle)) {
            var additionalIterationsNumber = settings.getAdditionalResolveCollisionsIterationsNumber();
            if (iterationNumber - 1 < additionalIterationsNumber) {
                if (battle.getModel().getUpdates().getRemoved() != null) {
                    battle.setMovingObjects(null);
                }
                process(battle, iterationNumber + 1);
            }
        }
    }

    private void detectAllCollisions(BattleCalculations battle, int iterationNumber) {
        battle.getMovingObjects().forEach(Calculations::clearCollisionsCheckedWith);
        battle.getMovingObjects().forEach(object ->
                detectors.forEach(detector ->
                        object.getCollisions(iterationNumber).addAll(detector.detect(object, battle, false))));
    }

    private void resolveStrongestCollisions(BattleCalculations battle) {
        battle.getMovingObjects().forEach(object -> {
            var collision = findStrongestCollision(object.getLastCollisions());
            if (collision != null && (firstObjectIsProjectile(collision)
                    || secondObjectDoesNotHaveGroundCollisions(collision))) {
                var shouldResolve = true;
                for (var preprocessor : preprocessors) {
                    var result = preprocessor.process(collision, battle);
                    if (result != null) {
                        shouldResolve = result;
                    }
                }
                if (shouldResolve) {
                    resolver.resolve(collision, battle.getModel());
                }
            }
        });
    }

    private void postprocessCollisions(BattleCalculations battle) {
        battle.getMovingObjects().forEach(object ->
                postprocessors.forEach(postprocessor ->
                        postprocessor.process(object, battle)));
    }

    private boolean collisionsExist(BattleCalculations battle) {
        for (var object : battle.getMovingObjects()) {
            if (!object.getLastCollisions().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Collision findStrongestCollision(Collection<Collision> collisions) {
        if (collisions.isEmpty()) {
            return null;
        }
        var iterator = collisions.iterator();
        var strongest = iterator.next();
        while (iterator.hasNext()) {
            var collision = iterator.next();
            var collisionVelocity = collision.getClosingVelocity();
            var strongestVelocity = strongest.getClosingVelocity();
            if (CollideObjectType.GROUND.equals(collision.getType())
                    && !CollideObjectType.GROUND.equals(strongest.getType())) {
                strongest = collision;
            } else if (collisionVelocity < 1.0 && strongestVelocity < 1.0
                    && collision.getContact().depth() > strongest.getContact().depth()) {
                strongest = collision;
            } else if (collisionVelocity > strongestVelocity) {
                strongest = collision;
            }
        }
        return strongest;
    }

    private boolean firstObjectIsProjectile(Collision collision) {
        var first = collision.getPair().first();
        return first instanceof ShellCalculations || first instanceof MissileCalculations;
    }

    private boolean secondObjectDoesNotHaveGroundCollisions(Collision collision) {
        var second = collision.getPair().second();
        if (second == null) {
            return true;
        }
        var secondCollisions = second.getLastCollisions();
        for (var secondCollision : secondCollisions) {
            if (CollideObjectType.GROUND.equals(secondCollision.getType())) {
                return false;
            }
        }
        return true;
    }
}
