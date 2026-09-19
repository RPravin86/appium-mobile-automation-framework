package io.github.rpravin86.mobile.config;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

public record FrameworkConfig(
        MobilePlatform platform,
        ServerMode serverMode,
        URI serverUri,
        String serverHost,
        int serverPort,
        String deviceName,
        String deviceUdid,
        Path appPath,
        String androidAppPackage,
        String androidAppActivity,
        boolean autoGrantPermissions,
        String iosBundleId,
        String xcodeOrgId,
        String xcodeSigningId,
        String updatedWdaBundleId,
        Duration newCommandTimeout,
        Duration elementWaitTimeout,
        Duration elementPollingInterval,
        boolean noReset,
        boolean fullReset
) {
    public FrameworkConfig {
        if (noReset && fullReset) {
            throw new IllegalArgumentException("appium.no.reset and appium.full.reset cannot both be true");
        }
        if (serverPort < 1 || serverPort > 65_535) {
            throw new IllegalArgumentException("appium.server.port must be between 1 and 65535");
        }
        if (deviceName == null || deviceName.isBlank()) {
            throw new IllegalArgumentException("device.name is required");
        }
        if (deviceUdid == null || deviceUdid.isBlank()) {
            throw new IllegalArgumentException("device.udid is required for deterministic real-device execution");
        }
        if (elementWaitTimeout.isZero() || elementWaitTimeout.isNegative()) {
            throw new IllegalArgumentException("interaction.wait.seconds must be greater than zero");
        }
        if (elementPollingInterval.isZero() || elementPollingInterval.isNegative()) {
            throw new IllegalArgumentException("interaction.poll.millis must be greater than zero");
        }
        if (elementPollingInterval.compareTo(elementWaitTimeout) > 0) {
            throw new IllegalArgumentException("interaction.poll.millis cannot exceed the element wait timeout");
        }
    }
}
