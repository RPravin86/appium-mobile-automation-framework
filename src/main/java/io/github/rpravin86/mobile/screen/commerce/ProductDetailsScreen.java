package io.github.rpravin86.mobile.screen.commerce;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.interaction.PlatformLocator;
import io.github.rpravin86.mobile.screen.BaseScreen;
import org.openqa.selenium.By;

public final class ProductDetailsScreen extends BaseScreen {

    private static final String ANDROID_PACKAGE = "com.saucelabs.mydemoapp.android:id/";

    private static final PlatformLocator ADD_TO_CART = new PlatformLocator(
            By.id(ANDROID_PACKAGE + "cartBt"),
            AppiumBy.accessibilityId("Add To Cart")
    );
    private static final PlatformLocator CART = new PlatformLocator(
            By.id(ANDROID_PACKAGE + "cartRL"),
            AppiumBy.accessibilityId("Cart-tab-item")
    );

    public ProductDetailsScreen(AppiumDriver driver, FrameworkConfig config) {
        super(driver, config);
    }

    public void addCurrentProductToCart() {
        actions.tap(locator(ADD_TO_CART));
    }

    public void openCart() {
        actions.tap(locator(CART));
    }
}
