package io.github.rpravin86.mobile.evidence;

import io.github.rpravin86.mobile.config.ConfigurationLoader;

public record EvidenceConfig(
        boolean videoEnabled,
        boolean keepRawVideo,
        int videoCrf,
        int videoMaxWidth
) {

    public static EvidenceConfig load() {
        return new EvidenceConfig(
                booleanProperty("evidence.video.enabled", true),
                booleanProperty("evidence.video.keep.raw", false),
                integerProperty("evidence.video.crf", 32),
                integerProperty("evidence.video.max.width", 720)
        );
    }

    public EvidenceConfig {
        if (videoCrf < 0 || videoCrf > 51) {
            throw new IllegalArgumentException("evidence.video.crf must be between 0 and 51");
        }
        if (videoMaxWidth < 320) {
            throw new IllegalArgumentException("evidence.video.max.width must be at least 320");
        }
    }

    private static boolean booleanProperty(String key, boolean defaultValue) {
        String value = ConfigurationLoader.resolve(key, Boolean.toString(defaultValue));
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException(key + " must be true or false, but was '" + value + "'");
        }
        return Boolean.parseBoolean(value);
    }

    private static int integerProperty(String key, int defaultValue) {
        String value = ConfigurationLoader.resolve(key, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(key + " must be an integer, but was '" + value + "'", exception);
        }
    }
}
