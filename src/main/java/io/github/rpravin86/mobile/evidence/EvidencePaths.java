package io.github.rpravin86.mobile.evidence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public final class EvidencePaths {

    public static final Path REPORT_ROOT = Path.of("reports").toAbsolutePath().normalize();
    public static final Path EXTENT_REPORT = REPORT_ROOT.resolve("extent/index.html");
    public static final Path SCREENSHOTS = REPORT_ROOT.resolve("evidence/screenshots");
    public static final Path VIDEOS = REPORT_ROOT.resolve("evidence/videos");

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private EvidencePaths() {
    }

    public static void createDirectories() {
        try {
            Files.createDirectories(EXTENT_REPORT.getParent());
            Files.createDirectories(SCREENSHOTS);
            Files.createDirectories(VIDEOS);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create test evidence directories", exception);
        }
    }

    public static Path screenshot(String scenarioName) {
        return SCREENSHOTS.resolve(fileStem(scenarioName) + ".png");
    }

    public static Path rawVideo(String scenarioName) {
        return VIDEOS.resolve(fileStem(scenarioName) + "-raw.mp4");
    }

    public static Path compressedVideo(Path rawVideo) {
        String rawName = rawVideo.getFileName().toString();
        return rawVideo.resolveSibling(rawName.replace("-raw.mp4", ".mp4"));
    }

    private static String fileStem(String scenarioName) {
        String normalized = scenarioName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        String safeName = normalized.isBlank() ? "scenario" : normalized;
        return safeName + "-" + TIMESTAMP.format(LocalDateTime.now()) + "-"
                + UUID.randomUUID().toString().substring(0, 8);
    }
}
