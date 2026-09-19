package io.github.rpravin86.mobile.screen.commerce;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.interaction.PlatformLocator;
import io.github.rpravin86.mobile.screen.BaseScreen;
import org.openqa.selenium.By;

public final class CatalogScreen extends BaseScreen {

    private static final String ANDROID_PACKAGE = "com.saucelabs.mydemoapp.android:id/";

    private static final PlatformLocator CATALOG = new PlatformLocator(
            By.id(ANDROID_PACKAGE + "productTV"),
            AppiumBy.accessibilityId("Catalog-screen")
    );
    private static final PlatformLocator FIRST_PRODUCT_NAME = new PlatformLocator(
            By.id(ANDROID_PACKAGE + "titleTV"),
            AppiumBy.accessibilityId("Product Name")
    );

    public CatalogScreen(AppiumDriver driver, FrameworkConfig config) {
        super(driver, config);
    }

    public boolean isLoaded() {
        return actions.isVisible(locator(CATALOG));
    }

    public String openFirstProduct() {
        By productName = locator(FIRST_PRODUCT_NAME);
        String selectedProduct = actions.readText(productName);
        actions.tap(productName);
        return selectedProduct;
    }
}
