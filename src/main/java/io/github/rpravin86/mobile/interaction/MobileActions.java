package io.github.rpravin86.mobile.interaction;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

public final class MobileActions {

    private static final Logger LOGGER = LogManager.getLogger(MobileActions.class);

    private final WebDriverWait wait;

    public MobileActions(AppiumDriver driver, Duration timeout, Duration pollingInterval) {
        Objects.requireNonNull(driver, "Appium driver is required");
        Objects.requireNonNull(timeout, "Element wait timeout is required");
        Objects.requireNonNull(pollingInterval, "Element polling interval is required");
        WebDriverWait configuredWait = new WebDriverWait(driver, timeout);
        configuredWait.pollingEvery(pollingInterval);
        this.wait = configuredWait;
    }

    public WebElement findVisible(By locator) {
        return timed("find visible element", locator,
                () -> wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.visibilityOfElementLocated(locator))));
    }

    public void tap(By locator) {
        timed("tap element", locator, () -> {
            wait.until(ExpectedConditions.refreshed(
                    ExpectedConditions.elementToBeClickable(locator))).click();
            return null;
        });
    }

    public void enterText(By locator, String value) {
        Objects.requireNonNull(value, "Text value is required");
        timed("enter text", locator, () -> {
            WebElement element = wait.until(ExpectedConditions.refreshed(
                    ExpectedConditions.elementToBeClickable(locator)));
            element.clear();
            element.sendKeys(value);
            return null;
        });
    }

    public String readText(By locator) {
        return timed("read element text", locator, () -> findVisible(locator).getText());
    }

    public boolean isVisible(By locator) {
        try {
            findVisible(locator);
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void waitUntilHidden(By locator) {
        timed("wait for element to disappear", locator, () -> {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            return null;
        });
    }

    private <T> T timed(String operation, By locator, Supplier<T> action) {
        Objects.requireNonNull(locator, "Element locator is required");
        long startedAt = System.nanoTime();
        try {
            return action.get();
        } finally {
            long elapsedMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
            LOGGER.debug("Mobile action '{}' completed in {} ms for {}", operation, elapsedMillis, locator);
        }
    }
}
