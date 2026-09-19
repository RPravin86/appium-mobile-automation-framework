package io.github.rpravin86.mobile.screen.commerce;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.interaction.PlatformLocator;
import io.github.rpravin86.mobile.screen.BaseScreen;
import org.openqa.selenium.By;

public final class CartScreen extends BaseScreen {

    private static final String ANDROID_PACKAGE = "com.saucelabs.mydemoapp.android:id/";

    private static final PlatformLocator CART = new PlatformLocator(
            By.id(ANDROID_PACKAGE + "productTV"),
            AppiumBy.accessibilityId("Cart-screen")
    );

    public CartScreen(AppiumDriver driver, FrameworkConfig config) {
        super(driver, config);
    }

    public boolean isLoaded() {
        return actions.isVisible(locator(CART));
    }

    public boolean containsProduct(String productName) {
        PlatformLocator product = new PlatformLocator(
                By.xpath("//*[@text=" + xpathLiteral(productName) + "]"),
                AppiumBy.accessibilityId(productName)
        );
        return actions.isVisible(locator(product));
    }

    private static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }
}
