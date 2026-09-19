package io.github.rpravin86.mobile.driver;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.config.ServerMode;

import java.net.URI;

public final class AppiumServerManager {

    private static final Object SERVICE_LOCK = new Object();
    private static AppiumDriverLocalService localService;

    private AppiumServerManager() {
    }

    public static URI resolveServerUri(FrameworkConfig config) {
        if (config.serverMode() == ServerMode.REMOTE) {
            return config.serverUri();
        }

        synchronized (SERVICE_LOCK) {
            if (localService == null || !localService.isRunning()) {
                localService = new AppiumServiceBuilder()
                        .withIPAddress(config.serverHost())
                        .usingPort(config.serverPort())
                        .build();
                localService.start();
            }
            return URI.create(localService.getUrl().toString());
        }
    }

    public static void stopLocalServer() {
        synchronized (SERVICE_LOCK) {
            if (localService != null) {
                localService.stop();
                localService = null;
            }
        }
    }
}
