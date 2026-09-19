package io.github.rpravin86.mobile.config;

import java.util.Locale;

public enum MobilePlatform {
    ANDROID,
    IOS;

    public static MobilePlatform from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("framework.platform must be android or ios");
        }

        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unsupported mobile platform '%s'. Use android or ios.".formatted(value), exception);
        }
    }
}
