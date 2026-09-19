package io.github.rpravin86.mobile.interaction;

import io.appium.java_client.AppiumDriver;
import io.github.rpravin86.mobile.config.MobilePlatform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import java.time.Duration;
import java.util.Objects;

public final class MobileGestures {

    private static final Logger LOGGER = LogManager.getLogger(MobileGestures.class);

    private final AppiumDriver driver;
    private final MobilePlatform platform;

    public MobileGestures(AppiumDriver driver, MobilePlatform platform) {
        this.driver = Objects.requireNonNull(driver, "Appium driver is required");
        this.platform = Objects.requireNonNull(platform, "Mobile platform is required");
    }

    public void swipe(SwipeDirection direction, double distancePercent) {
        Dimension size = driver.manage().window().getSize();
        GestureCommand command = GestureCommandFactory.swipe(
                platform,
                direction,
                new Rectangle(new Point(0, 0), size),
                distancePercent
        );

        long startedAt = System.nanoTime();
        try {
            driver.executeScript(command.script(), command.arguments());
        } finally {
            long elapsedMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
            LOGGER.debug("{} swipe completed in {} ms", direction, elapsedMillis);
        }
    }
}
