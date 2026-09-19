package io.github.rpravin86.mobile.interaction;

import io.github.rpravin86.mobile.config.MobilePlatform;
import org.openqa.selenium.Rectangle;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class GestureCommandFactory {

    private GestureCommandFactory() {
    }

    public static GestureCommand swipe(
            MobilePlatform platform,
            SwipeDirection direction,
            Rectangle viewport,
            double distancePercent
    ) {
        Objects.requireNonNull(platform, "Mobile platform is required");
        Objects.requireNonNull(direction, "Swipe direction is required");
        Objects.requireNonNull(viewport, "Viewport is required");
        if (distancePercent <= 0 || distancePercent > 1) {
            throw new IllegalArgumentException("Swipe distance percent must be greater than 0 and at most 1");
        }
        if (viewport.getWidth() < 3 || viewport.getHeight() < 3) {
            throw new IllegalArgumentException("Swipe viewport must be at least 3 by 3 pixels");
        }

        if (platform == MobilePlatform.IOS) {
            return new GestureCommand(
                    "mobile: swipe",
                    Map.of("direction", direction.capabilityValue())
            );
        }

        int horizontalInset = Math.max(1, viewport.getWidth() / 10);
        int verticalInset = Math.max(1, viewport.getHeight() / 10);
        Map<String, Object> arguments = new LinkedHashMap<>();
        arguments.put("left", viewport.getX() + horizontalInset);
        arguments.put("top", viewport.getY() + verticalInset);
        arguments.put("width", viewport.getWidth() - (horizontalInset * 2));
        arguments.put("height", viewport.getHeight() - (verticalInset * 2));
        arguments.put("direction", direction.capabilityValue());
        arguments.put("percent", distancePercent);
        return new GestureCommand("mobile: swipeGesture", Map.copyOf(arguments));
    }
}
