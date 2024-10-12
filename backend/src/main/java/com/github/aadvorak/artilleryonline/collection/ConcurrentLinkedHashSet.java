package com.github.aadvorak.artilleryonline.collection;

import java.util.*;

public class ConcurrentLinkedHashSet<T> {

    private final Set<T> set = new LinkedHashSet<>();
    private final Map<T, Long> addTimes = new LinkedHashMap<>();

    public void add(T element) {
        synchronized (set) {
            set.add(element);
            addTimes.put(element, System.currentTimeMillis());
        }
    }

    public T poll() {
        synchronized (set) {
            if (set.isEmpty()) {
                return null;
            }
            Iterator<T> iterator = set.iterator();
            T next = iterator.next();
            iterator.remove();
            addTimes.remove(next);
            return next;
        }
    }

    public void remove(T element) {
        synchronized (set) {
            set.remove(element);
            addTimes.remove(element);
        }
    }

    public T pick() {
        if (set.isEmpty()) {
            return null;
        }
        Iterator<T> iterator = set.iterator();
        return iterator.next();
    }

    public Long getAddTime(T element) {
        return addTimes.get(element);
    }

    public int size() {
        return set.size();
    }
}
