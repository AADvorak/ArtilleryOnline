package com.github.aadvorak.artilleryonline.battle.collision;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.resolver.CollisionResolver;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CollisionsProcessor {

    private final Set<CollisionsDetector> detectors;

    private final Set<CollisionPreprocessor> preprocessors;

    private final Set<CollisionPostprocessor> postprocessors;

    private final CollisionResolver resolver;

    public void process(BattleCalculations battle) {
        detectAllCollisions(battle);
        resolveStrongestCollisions(battle);
        if (collisionsExist(battle)) {
            checkCollisionsResolved(battle);
            postprocessCollisions(battle);
        }
    }

    private void detectAllCollisions(BattleCalculations battle) {
        battle.getObjects().forEach(object ->
                detectors.forEach(detector ->
                        object.getCollisions().addAll(detector.detect(object, battle, false))));
    }

    private void resolveStrongestCollisions(BattleCalculations battle) {
        battle.getObjects().forEach(object -> {
            var collision = findStrongestCollision(object.getCollisions());
            if (collision != null) {
                var shouldResolve = true;
                for (var preprocessor : preprocessors) {
                    shouldResolve = preprocessor.process(collision, battle);
                }
                if (shouldResolve) {
                    resolver.resolve(collision, battle.getModel().getCurrentTimeStepSecs());
                    // todo check this logic
                    object.setHasCollisions(true);
                    if (collision.getPair().second() != null) {
                        collision.getPair().second().setHasCollisions(true);
                    }
                }
            }
        });
    }

    private void postprocessCollisions(BattleCalculations battle) {
        battle.getObjects().forEach(object ->
                postprocessors.forEach(postprocessor ->
                        postprocessor.process(object, battle)));
    }

    private boolean collisionsExist(BattleCalculations battle) {
        for (var object : battle.getObjects()) {
            if (object.isHasCollisions()) {
                return true;
            }
        }
        return false;
    }

    private void checkCollisionsResolved(BattleCalculations battle) {
        battle.getObjects().forEach(object -> {
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
