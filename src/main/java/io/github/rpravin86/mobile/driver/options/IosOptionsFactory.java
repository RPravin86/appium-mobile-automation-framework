package io.github.rpravin86.mobile.driver.options;

import io.appium.java_client.ios.options.XCUITestOptions;
import io.github.rpravin86.mobile.config.FrameworkConfig;

public final class IosOptionsFactory {

    private static final String XCODE_ORG_ID = "appium:xcodeOrgId";
    private static final String XCODE_SIGNING_ID = "appium:xcodeSigningId";

    private IosOptionsFactory() {
    }

    public static XCUITestOptions create(FrameworkConfig config) {
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(config.deviceName())
                .setUdid(config.deviceUdid())
                .setNewCommandTimeout(config.newCommandTimeout())
                .setNoReset(config.noReset())
                .setFullReset(config.fullReset());

        if (config.appPath() != null) {
            options.setApp(config.appPath().toString());
        }
        if (!config.iosBundleId().isBlank()) {
            options.setBundleId(config.iosBundleId());
        }
        if (!config.xcodeOrgId().isBlank()) {
            options.setCapability(XCODE_ORG_ID, config.xcodeOrgId());
        }
        if (!config.xcodeSigningId().isBlank()) {
            options.setCapability(XCODE_SIGNING_ID, config.xcodeSigningId());
        }
        if (!config.updatedWdaBundleId().isBlank()) {
            options.setUpdatedWdaBundleId(config.updatedWdaBundleId());
        }

        return options;
    }
}
