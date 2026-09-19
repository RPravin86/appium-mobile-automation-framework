package io.github.rpravin86.mobile.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.driver.options.AndroidOptionsFactory;
import io.github.rpravin86.mobile.driver.options.IosOptionsFactory;

import java.net.MalformedURLException;
import java.net.URL;

public final class MobileDriverFactory {

    private MobileDriverFactory() {
    }

    public static AppiumDriver create(FrameworkConfig config) {
        URL serverUrl = toUrl(AppiumServerManager.resolveServerUri(config));

        return switch (config.platform()) {
            case ANDROID -> new AndroidDriver(serverUrl, AndroidOptionsFactory.create(config));
            case IOS -> new IOSDriver(serverUrl, IosOptionsFactory.create(config));
        };
    }

    private static URL toUrl(java.net.URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException("Invalid Appium server URL: " + uri, exception);
        }
    }
}
