package io.github.rpravin86.mobile.evidence;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.github.rpravin86.mobile.config.MobilePlatform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

public final class VideoEvidenceRecorder {

    private static final Logger LOGGER = LogManager.getLogger(VideoEvidenceRecorder.class);
    private static final Duration MAX_RECORDING_TIME = Duration.ofMinutes(10);
    private static final ThreadLocal<Boolean> RECORDING = ThreadLocal.withInitial(() -> false);

    private VideoEvidenceRecorder() {
    }

    public static void start(AppiumDriver driver, MobilePlatform platform, EvidenceConfig config) {
        if (!config.videoEnabled() || !(driver instanceof CanRecordScreen recorder)) {
            return;
        }

        try {
            if (platform == MobilePlatform.ANDROID) {
                recorder.startRecordingScreen(AndroidStartScreenRecordingOptions.startScreenRecordingOptions()
                        .withBitRate(1_000_000)
                        .withTimeLimit(MAX_RECORDING_TIME));
            } else {
                recorder.startRecordingScreen(IOSStartScreenRecordingOptions.startScreenRecordingOptions()
                        .withVideoType("libx264")
                        .withFps(10)
                        .withTimeLimit(MAX_RECORDING_TIME));
            }
            RECORDING.set(true);
            LOGGER.info("Started screen recording");
        } catch (RuntimeException exception) {
            RECORDING.remove();
            LOGGER.warn("Screen recording could not be started; scenario execution will continue", exception);
        }
    }

    public static Optional<Path> stop(AppiumDriver driver, String scenarioName, EvidenceConfig config) {
        if (!RECORDING.get() || !(driver instanceof CanRecordScreen recorder)) {
            RECORDING.remove();
            return Optional.empty();
        }

        try {
            String encodedVideo = recorder.stopRecordingScreen();
            if (encodedVideo == null || encodedVideo.isBlank()) {
                LOGGER.warn("Appium returned an empty screen recording");
                return Optional.empty();
            }

            Path rawVideo = EvidencePaths.rawVideo(scenarioName);
            Files.write(rawVideo, Base64.getDecoder().decode(encodedVideo));
            return Optional.of(VideoCompressor.compress(rawVideo, config));
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Screen recording could not be saved; scenario evidence will continue without video", exception);
            return Optional.empty();
        } finally {
            RECORDING.remove();
        }
    }
}
