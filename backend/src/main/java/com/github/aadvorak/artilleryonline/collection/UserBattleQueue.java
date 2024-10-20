package com.github.aadvorak.artilleryonline.collection;

import java.util.*;

public class UserBattleQueue {

    private final Map<Long, UserBattleQueueElement> map = new LinkedHashMap<>();

    public synchronized void add(UserBattleQueueElement element) {
        map.put(element.getUser().getId(), element);
    }

    public synchronized UserBattleQueueElement poll() {
        if (map.isEmpty()) {
            return null;
        }
        Iterator<Long> iterator = map.keySet().iterator();
        Long next = iterator.next();
        var element = map.get(next);
        iterator.remove();
        return element;
    }

    public synchronized void remove(Long userId) {
        map.remove(userId);
    }

    public UserBattleQueueElement pick() {
        if (map.isEmpty()) {
            return null;
        }
        Iterator<Long> iterator = map.keySet().iterator();
        return map.get(iterator.next());
    }

    public Long getAddTime(Long userId) {
        var element = map.get(userId);
        if (element == null) {
            return null;
        }
        return element.getAddTime();
    }

    public int size() {
        return map.size();
    }
}
