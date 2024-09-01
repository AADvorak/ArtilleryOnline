package com.github.aadvorak.artilleryonline.collection;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class ConcurrentLinkedHashSet<T> {

    private final Set<T> set = new LinkedHashSet<>();

    public synchronized void add(T element) {
        set.add(element);
    }

    public synchronized T poll() {
        if (set.isEmpty()) {
            return null;
        }
        Iterator<T> iterator = set.iterator();
        T next = iterator.next();
        iterator.remove();
        return next;
    }
}
