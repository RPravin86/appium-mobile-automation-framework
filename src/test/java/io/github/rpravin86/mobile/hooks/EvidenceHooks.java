package io.github.rpravin86.mobile.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.rpravin86.mobile.config.ConfigurationLoader;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.driver.DriverManager;
import io.github.rpravin86.mobile.evidence.EvidenceConfig;
import io.github.rpravin86.mobile.evidence.ScreenshotEvidence;
import io.github.rpravin86.mobile.evidence.VideoEvidenceRecorder;
import io.github.rpravin86.mobile.reporting.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EvidenceHooks {

    private static final Logger LOGGER = LogManager.getLogger(EvidenceHooks.class);

    private final EvidenceConfig evidenceConfig = EvidenceConfig.load();

    @Before(value = "@device", order = 100)
    public void startVideoRecording() {
        FrameworkConfig config = ConfigurationLoader.load();
        VideoEvidenceRecorder.start(DriverManager.getDriver(), config.platform(), evidenceConfig);
    }

    @After(value = "@device", order = 200)
    public void captureDeviceEvidence(Scenario scenario) {
        if (!DriverManager.hasActiveSession()) {
            return;
        }

        if (scenario.isFailed()) {
            try {
                ExtentReportManager.attachScreenshot(
                        ScreenshotEvidence.capture(DriverManager.getDriver(), scenario.getName())
                );
            } catch (RuntimeException exception) {
                LOGGER.warn("Failure screenshot could not be captured", exception);
                reportWarning("Failure screenshot could not be captured: " + exception.getMessage());
            }
        }

        VideoEvidenceRecorder.stop(DriverManager.getDriver(), scenario.getName(), evidenceConfig)
                .ifPresent(video -> {
                    try {
                        ExtentReportManager.attachVideo(video);
                    } catch (RuntimeException exception) {
                        LOGGER.warn("Screen recording could not be attached to the Extent report", exception);
                    }
                });
    }

    private static void reportWarning(String message) {
        try {
            ExtentReportManager.warning(message);
        } catch (RuntimeException exception) {
            LOGGER.warn("Evidence warning could not be added to the Extent report", exception);
        }
    }
}
