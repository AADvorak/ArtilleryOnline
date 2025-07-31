package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class CalculationsBase {

    private final Map<Integer, Set<Collision>> collisionMap = new HashMap<>();

    @Getter @Setter
    private boolean hasCollisions;

    public Set<Collision> getCollisions() {
        var maxKey = collisionMap.keySet().stream().max(Integer::compareTo).orElse(1);
        return getCollisions(maxKey);
    }

    public Set<Collision> getCollisions(int iterationNumber) {
        if (!collisionMap.containsKey(iterationNumber)) {
            collisionMap.put(iterationNumber, new HashSet<>());
        }
        return collisionMap.get(iterationNumber);
    }
}
