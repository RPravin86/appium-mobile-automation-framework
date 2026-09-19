package io.github.rpravin86.mobile.steps;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.rpravin86.mobile.config.ConfigurationLoader;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.config.MobilePlatform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationSteps {

    private Path configurationFile;
    private FrameworkConfig configuration;
    private RuntimeException configurationFailure;
    private String previousDeviceName;
    private String previousPlatform;
    private boolean deviceNameOverridden;
    private boolean platformOverrideCleared;

    @Given("a valid Android configuration file")
    public void createAndroidConfiguration() throws IOException {
        configurationFile = writeConfiguration("android", "File Device");
    }

    @Given("the device name is overridden with a JVM property")
    public void overrideDeviceName() {
        previousDeviceName = System.getProperty("device.name");
        deviceNameOverridden = true;
        System.setProperty("device.name", "Property Device");
    }

    @Given("a configuration file with platform {string}")
    public void createConfigurationWithPlatform(String platform) throws IOException {
        previousPlatform = System.getProperty("framework.platform");
        platformOverrideCleared = previousPlatform != null;
        System.clearProperty("framework.platform");
        configurationFile = writeConfiguration(platform, "Test Device");
    }

    @When("the framework configuration is loaded")
    public void loadConfiguration() {
        configuration = ConfigurationLoader.load(configurationFile);
    }

    @When("the framework configuration is loaded expecting a failure")
    public void loadInvalidConfiguration() {
        try {
            ConfigurationLoader.load(configurationFile);
        } catch (RuntimeException exception) {
            configurationFailure = exception;
        }
    }

    @Then("the overridden device name is used")
    public void verifyOverriddenDeviceName() {
        assertEquals("Property Device", configuration.deviceName());
    }

    @Then("the configured platform is Android")
    public void verifyAndroidPlatform() {
        assertEquals(MobilePlatform.ANDROID, configuration.platform());
    }

    @Then("the configuration error contains {string}")
    public void verifyConfigurationError(String expectedMessage) {
        assertNotNull(configurationFailure, "Expected configuration loading to fail");
        assertTrue(configurationFailure.getMessage().contains(expectedMessage));
    }

    @After("@foundation")
    public void clearScenarioOverrides() throws IOException {
        if (deviceNameOverridden) {
            if (previousDeviceName == null) {
                System.clearProperty("device.name");
            } else {
                System.setProperty("device.name", previousDeviceName);
            }
        }
        if (platformOverrideCleared) {
            System.setProperty("framework.platform", previousPlatform);
        }
        if (configurationFile != null) {
            Files.deleteIfExists(configurationFile);
        }
    }

    private Path writeConfiguration(String platform, String deviceName) throws IOException {
        Path file = Files.createTempFile("mobile-framework-", ".properties");
        Files.writeString(file, """
                framework.platform=%s
                device.name=%s
                device.udid=test-device-udid
                """.formatted(platform, deviceName));
        return file;
    }
}
