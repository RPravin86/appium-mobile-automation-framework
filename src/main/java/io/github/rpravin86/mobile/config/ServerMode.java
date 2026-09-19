package io.github.rpravin86.mobile.config;

import java.util.Locale;

public enum ServerMode {
    LOCAL,
    REMOTE;

    public static ServerMode from(String value) {
        if (value == null || value.isBlank()) {
            return REMOTE;
        }

        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unsupported Appium server mode '%s'. Use local or remote.".formatted(value), exception);
        }
    }
}
