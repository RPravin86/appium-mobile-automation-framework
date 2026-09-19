package io.github.rpravin86.mobile.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.rpravin86.mobile.evidence.EvidencePaths;
import io.github.rpravin86.mobile.evidence.ScreenshotEvidence.CapturedScreenshot;

import java.nio.file.Path;
import java.util.Collection;

public final class ExtentReportManager {

    private static final ExtentReports REPORT = createReport();
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    public static synchronized void startScenario(String name, String description, Collection<String> tags) {
        ExtentTest test = REPORT.createTest(name, description);
        tags.stream()
                .map(tag -> tag.startsWith("@") ? tag.substring(1) : tag)
                .filter(tag -> !tag.isBlank())
                .forEach(test::assignCategory);
        CURRENT_TEST.set(test);
    }

    public static synchronized void passStep(String step) {
        current().pass(step);
    }

    public static synchronized void skipStep(String step) {
        current().skip(step);
    }

    public static synchronized void failStep(String step, Throwable failure) {
        current().fail(step);
        if (failure != null) {
            current().fail(failure);
        }
    }

    public static synchronized void scenarioPassed(long elapsedMillis) {
        current().pass("Scenario completed in " + elapsedMillis + " ms");
    }

    public static synchronized void scenarioSkipped(long elapsedMillis) {
        current().skip("Scenario skipped after " + elapsedMillis + " ms");
    }

    public static synchronized void scenarioFailed(long elapsedMillis, Throwable failure) {
        current().fail("Scenario failed after " + elapsedMillis + " ms");
        if (failure != null) {
            current().fail(failure);
        }
    }

    public static synchronized void attachScreenshot(CapturedScreenshot screenshot) {
        current().fail(
                "Failure screenshot: " + screenshot.path().getFileName(),
                MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot.base64()).build()
        );
    }

    public static synchronized void attachVideo(Path video) {
        Path reportDirectory = EvidencePaths.EXTENT_REPORT.getParent();
        String relativePath = reportDirectory.relativize(video.toAbsolutePath().normalize())
                .toString()
                .replace('\\', '/');
        current().info("""
                <div class="video-evidence">
                  <p>Screen recording: %s</p>
                  <video controls preload="metadata" style="max-width:720px;width:100%%;">
                    <source src="%s" type="video/mp4">
                  </video>
                </div>
                """.formatted(video.getFileName(), relativePath));
    }

    public static synchronized void warning(String message) {
        current().warning(message);
    }

    public static synchronized void finishScenario() {
        CURRENT_TEST.remove();
    }

    public static synchronized void flush() {
        REPORT.flush();
    }

    private static ExtentTest current() {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            throw new IllegalStateException("No Extent scenario is active on this thread");
        }
        return test;
    }

    private static ExtentReports createReport() {
        EvidencePaths.createDirectories();
        ExtentSparkReporter spark = new ExtentSparkReporter(EvidencePaths.EXTENT_REPORT.toString());
        spark.config().setDocumentTitle("Mobile Automation Results");
        spark.config().setReportName("Android and iOS BDD Execution");
        spark.config().setTheme(Theme.STANDARD);

        ExtentReports report = new ExtentReports();
        report.attachReporter(spark);
        report.setSystemInfo("Java", System.getProperty("java.version"));
        report.setSystemInfo("Operating system", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        return report;
    }
}
