package com.github.aadvorak.artilleryonline.collection;

import java.util.LinkedList;
import java.util.List;

public class LimitedQueue<E> {

    private final LinkedList<E> list;

    private final int maxSize;

    public LimitedQueue(int maxSize) {
        this.list = new LinkedList<>();
        this.maxSize = maxSize;
    }

    public void add(E element) {
        if (list.size() >= maxSize) {
            list.removeFirst();
        }
        list.addLast(element);
    }

    public List<E> getAll() {
        return list;
    }

    public E get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        list.clear();
    }
}
