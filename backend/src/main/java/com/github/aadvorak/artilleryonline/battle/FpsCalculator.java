package com.github.aadvorak.artilleryonline.battle;

public class FpsCalculator {

    private final int[] buffer;
    private final int size;
    private int currentIndex;
    private boolean isFull;

    public FpsCalculator(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size must be positive");
        }
        this.buffer = new int[size];
        this.currentIndex = 0;
        this.size = size;
        this.isFull = false;
    }

    public void addTimeStep(int value) {
        buffer[currentIndex] = value;
        currentIndex = (currentIndex + 1) % size;
        if (currentIndex == 0) {
            isFull = true;
        }
    }

    public int getFps() {
        int sum = 0;
        int count = isFull ? size : currentIndex;

        if (count == 0) {
            return 0;
        }

        for (int i = 0; i < count; i++) {
            sum += buffer[(currentIndex + i) % size];
        }

        if (sum == 0) {
            return 0;
        }

        return 1000 * count / sum;
    }
}
