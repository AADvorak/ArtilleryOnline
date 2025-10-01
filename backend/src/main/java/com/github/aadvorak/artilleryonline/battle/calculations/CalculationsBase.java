package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.collision.Collision;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CalculationsBase {

    private final Set<Integer> collisionsCheckedWith = new HashSet<>();

    private final Map<Integer, Set<Collision>> collisionMap = new HashMap<>();

    public Set<Collision> getLastCollisions() {
        var maxKey = collisionMap.keySet().stream().max(Integer::compareTo).orElse(1);
        return getCollisions(maxKey);
    }

    public Set<Collision> getCollisions(int iterationNumber) {
        if (!collisionMap.containsKey(iterationNumber)) {
            collisionMap.put(iterationNumber, new HashSet<>());
        }
        return collisionMap.get(iterationNumber);
    }

    public Set<Collision> getAllCollisions() {
        return collisionMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    public void addCollisionsCheckedWith(int id) {
        collisionsCheckedWith.add(id);
    }

    public void clearCollisionsCheckedWith() {
        collisionsCheckedWith.clear();
    }

    public boolean collisionsNotCheckedWith(int id) {
        return !collisionsCheckedWith.contains(id);
    }
}
