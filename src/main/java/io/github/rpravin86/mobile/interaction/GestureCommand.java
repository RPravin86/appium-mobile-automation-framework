package io.github.rpravin86.mobile.interaction;

import java.util.Map;
import java.util.Objects;

public record GestureCommand(String script, Map<String, Object> arguments) {

    public GestureCommand {
        Objects.requireNonNull(script, "Gesture script is required");
        arguments = Map.copyOf(Objects.requireNonNull(arguments, "Gesture arguments are required"));
    }
}
