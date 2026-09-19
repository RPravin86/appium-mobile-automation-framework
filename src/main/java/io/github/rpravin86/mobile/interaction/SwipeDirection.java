package io.github.rpravin86.mobile.interaction;

public enum SwipeDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public String capabilityValue() {
        return name().toLowerCase();
    }
}
