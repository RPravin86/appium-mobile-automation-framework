package io.github.rpravin86.mobile.driver.options;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.github.rpravin86.mobile.config.FrameworkConfig;

public final class AndroidOptionsFactory {

    private AndroidOptionsFactory() {
    }

    public static UiAutomator2Options create(FrameworkConfig config) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(config.deviceName())
                .setUdid(config.deviceUdid())
                .setNewCommandTimeout(config.newCommandTimeout())
                .setNoReset(config.noReset())
                .setFullReset(config.fullReset())
                .setAutoGrantPermissions(config.autoGrantPermissions());

        if (config.appPath() != null) {
            options.setApp(config.appPath().toString());
        }
        if (!config.androidAppPackage().isBlank()) {
            options.setAppPackage(config.androidAppPackage());
        }
        if (!config.androidAppActivity().isBlank()) {
            options.setAppActivity(config.androidAppActivity());
        }

        return options;
    }
}
