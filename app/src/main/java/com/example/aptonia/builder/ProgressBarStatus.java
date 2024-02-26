package com.example.aptonia.builder;

public class ProgressBarStatus {

    private int current;
    private final int max;

    public ProgressBarStatus(int current, int max) {
        this.current = current;
        this.max = max;
    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
