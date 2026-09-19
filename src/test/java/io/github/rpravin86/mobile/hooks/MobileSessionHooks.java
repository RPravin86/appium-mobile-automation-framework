package io.github.rpravin86.mobile.hooks;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.github.rpravin86.mobile.config.ConfigurationLoader;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.context.CommerceScenarioContext;
import io.github.rpravin86.mobile.driver.AppiumServerManager;
import io.github.rpravin86.mobile.driver.DriverManager;

public final class MobileSessionHooks {

    private final CommerceScenarioContext scenarioContext;

    public MobileSessionHooks(CommerceScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(value = "@device", order = 0)
    public void startMobileSession() {
        FrameworkConfig config = ConfigurationLoader.load();
        DriverManager.startSession(config);
        scenarioContext.initialize(DriverManager.getDriver(), config);
    }

    @After(value = "@device", order = 100)
    public void stopMobileSession() {
        DriverManager.quitSession();
    }

    @AfterAll
    public static void stopManagedAppiumServer() {
        AppiumServerManager.stopLocalServer();
    }
}
