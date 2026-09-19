package io.github.rpravin86.mobile.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FoundationSteps {

    @Given("the automation framework is running on Java {int}")
    public void verifyJavaRuntime(int expectedFeatureVersion) {
        assertEquals(expectedFeatureVersion, Runtime.version().feature(),
                "The framework is running with an unsupported Java version");
    }

    @Then("the test process uses UTF-8 encoding")
    public void verifyProcessEncoding() {
        assertTrue(System.getProperty("file.encoding").equalsIgnoreCase("UTF-8"),
                "Test output must use UTF-8 encoding");
    }
}
