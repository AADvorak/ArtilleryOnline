package com.github.aadvorak.artilleryonline.battle.collision;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
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
    }

    public void process(BattleCalculations battle, int iterationNumber) {
        detectAllCollisions(battle, iterationNumber);
        resolveStrongestCollisions(battle);
        if (collisionsExist(battle)) {
            var additionalIterationsNumber = settings.getAdditionalResolveCollisionsIterationsNumber();
            if (iterationNumber - 1 >= additionalIterationsNumber) {
                checkCollisionsResolved(battle);
                postprocessCollisions(battle);
            } else {
                process(battle, iterationNumber + 1);
            }
        }
    }

    private void detectAllCollisions(BattleCalculations battle, int iterationNumber) {
        battle.getMovingObjects().forEach(object ->
                detectors.forEach(detector ->
                        object.getCollisions(iterationNumber).addAll(detector.detect(object, battle, false))));
    }

    private void resolveStrongestCollisions(BattleCalculations battle) {
        battle.getMovingObjects().forEach(object -> {
            var collision = findStrongestCollision(object.getLastCollisions());
            if (collision != null) {
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
                // todo check this logic
                object.setHasCollisions(true);
                if (collision.getPair().second() != null) {
                    collision.getPair().second().setHasCollisions(true);
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
            if (object.isHasCollisions()) {
                return true;
            }
        }
        return false;
    }

    private void checkCollisionsResolved(BattleCalculations battle) {
        battle.getMovingObjects().forEach(object -> {
            object.setHasCollisions(false);
            for (var detector : detectors) {
                var collisions = detector.detect(object, battle, true);
                if (!collisions.isEmpty()) {
                    object.setHasCollisions(true);
                    break;
                }
            }
        });
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
            if (collisionVelocity < 1.0 && strongestVelocity < 1.0
                    && collision.getContact().depth() > strongest.getContact().depth()) {
                strongest = collision;
            } else if (collisionVelocity > strongestVelocity) {
                strongest = collision;
            }
        }
        return strongest;
    }
}
