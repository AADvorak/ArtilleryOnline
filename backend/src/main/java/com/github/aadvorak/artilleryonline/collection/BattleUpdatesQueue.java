package com.github.aadvorak.artilleryonline.collection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BattleUpdatesQueue {
    private final BlockingQueue<BattleUpdatesQueueElement> queue;

    public BattleUpdatesQueue() {
        this(20);
    }

    public BattleUpdatesQueue(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    public void add(BattleUpdatesQueueElement element) {
        try {
            queue.put(element);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public BattleUpdatesQueueElement take() throws InterruptedException {
        return queue.take();
    }
}
