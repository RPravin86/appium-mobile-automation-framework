package io.github.rpravin86.mobile.interaction;

import io.github.rpravin86.mobile.config.MobilePlatform;
import org.openqa.selenium.By;

import java.util.Objects;

public record PlatformLocator(By android, By ios) {

    public PlatformLocator {
        Objects.requireNonNull(android, "Android locator is required");
        Objects.requireNonNull(ios, "iOS locator is required");
    }

    public By resolve(MobilePlatform platform) {
        Objects.requireNonNull(platform, "Mobile platform is required");
        return switch (platform) {
            case ANDROID -> android;
            case IOS -> ios;
        };
    }
}
