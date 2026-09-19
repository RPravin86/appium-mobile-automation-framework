package io.github.rpravin86.mobile.evidence;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public final class ScreenshotEvidence {

    private ScreenshotEvidence() {
    }

    public static CapturedScreenshot capture(AppiumDriver driver, String scenarioName) {
        byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
        Path destination = EvidencePaths.screenshot(scenarioName);
        try {
            Files.write(destination, screenshot);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save failure screenshot to " + destination, exception);
        }
        return new CapturedScreenshot(destination, Base64.getEncoder().encodeToString(screenshot));
    }

    public record CapturedScreenshot(Path path, String base64) {
    }
}
