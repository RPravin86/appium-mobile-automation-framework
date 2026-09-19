package io.github.rpravin86.mobile.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.rpravin86.mobile.config.MobilePlatform;
import io.github.rpravin86.mobile.interaction.GestureCommand;
import io.github.rpravin86.mobile.interaction.GestureCommandFactory;
import io.github.rpravin86.mobile.interaction.PlatformLocator;
import io.github.rpravin86.mobile.interaction.SwipeDirection;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InteractionSteps {

    private PlatformLocator platformLocator;
    private By resolvedLocator;
    private Rectangle viewport;
    private GestureCommand gestureCommand;

    @Given("a platform locator with Android id {string} and iOS id {string}")
    public void createPlatformLocator(String androidId, String iosId) {
        platformLocator = new PlatformLocator(By.id(androidId), By.id(iosId));
    }

    @When("the locator is resolved for {string}")
    public void resolveLocator(String platform) {
        resolvedLocator = platformLocator.resolve(MobilePlatform.from(platform));
    }

    @Then("the resolved locator contains {string}")
    public void verifyResolvedLocator(String expectedValue) {
        assertTrue(resolvedLocator.toString().contains(expectedValue));
    }

    @Given("a mobile viewport that is {int} by {int} pixels")
    public void createViewport(int width, int height) {
        viewport = new Rectangle(new Point(0, 0), new Dimension(width, height));
    }

    @When("an Android {word} swipe command is built for {double} percent")
    public void buildAndroidSwipe(String direction, double percent) {
        gestureCommand = GestureCommandFactory.swipe(
                MobilePlatform.ANDROID,
                SwipeDirection.valueOf(direction.toUpperCase()),
                viewport,
                percent
        );
    }

    @Then("the UiAutomator2 swipe uses the safe viewport bounds")
    public void verifyAndroidSwipeBounds() {
        assertEquals("mobile: swipeGesture", gestureCommand.script());
        assertEquals(100, gestureCommand.arguments().get("left"));
        assertEquals(200, gestureCommand.arguments().get("top"));
        assertEquals(800, gestureCommand.arguments().get("width"));
        assertEquals(1_600, gestureCommand.arguments().get("height"));
        assertEquals("up", gestureCommand.arguments().get("direction"));
        assertEquals(0.75, gestureCommand.arguments().get("percent"));
    }

    @Then("a swipe percentage above one is rejected")
    public void rejectInvalidSwipePercentage() {
        assertThrows(IllegalArgumentException.class, () -> GestureCommandFactory.swipe(
                MobilePlatform.ANDROID,
                SwipeDirection.UP,
                viewport,
                1.01
        ));
    }
}
