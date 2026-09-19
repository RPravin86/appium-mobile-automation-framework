package io.github.rpravin86.mobile.driver;

import io.appium.java_client.AppiumDriver;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DriverManager {

    private static final Logger LOGGER = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void startSession(FrameworkConfig config) {
        if (DRIVER.get() != null) {
            throw new IllegalStateException("A mobile session already exists on this thread");
        }

        AppiumDriver driver = MobileDriverFactory.create(config);
        DRIVER.set(driver);
        LOGGER.info("Started {} session {} on device {}",
                config.platform(), driver.getSessionId(), config.deviceName());
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No mobile session exists on this thread");
        }
        return driver;
    }

    public static boolean hasActiveSession() {
        return DRIVER.get() != null;
    }

    public static void quitSession() {
        AppiumDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                LOGGER.info("Stopping mobile session {}", driver.getSessionId());
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }
}
