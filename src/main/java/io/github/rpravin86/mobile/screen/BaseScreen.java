package io.github.rpravin86.mobile.screen;

import io.appium.java_client.AppiumDriver;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.interaction.MobileActions;
import io.github.rpravin86.mobile.interaction.MobileGestures;
import io.github.rpravin86.mobile.interaction.PlatformLocator;
import org.openqa.selenium.By;

import java.util.Objects;

public abstract class BaseScreen {

    protected final MobileActions actions;
    protected final MobileGestures gestures;

    private final FrameworkConfig config;

    protected BaseScreen(AppiumDriver driver, FrameworkConfig config) {
        Objects.requireNonNull(driver, "Appium driver is required");
        this.config = Objects.requireNonNull(config, "Framework configuration is required");
        this.actions = new MobileActions(
                driver,
                config.elementWaitTimeout(),
                config.elementPollingInterval()
        );
        this.gestures = new MobileGestures(driver, config.platform());
    }

    protected By locator(PlatformLocator locator) {
        return locator.resolve(config.platform());
    }
}
