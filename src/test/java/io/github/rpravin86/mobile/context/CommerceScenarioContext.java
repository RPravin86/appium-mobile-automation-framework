package io.github.rpravin86.mobile.context;

import io.appium.java_client.AppiumDriver;
import io.github.rpravin86.mobile.config.FrameworkConfig;
import io.github.rpravin86.mobile.screen.commerce.CartScreen;
import io.github.rpravin86.mobile.screen.commerce.CatalogScreen;
import io.github.rpravin86.mobile.screen.commerce.ProductDetailsScreen;

public final class CommerceScenarioContext {

    private CatalogScreen catalog;
    private ProductDetailsScreen productDetails;
    private CartScreen cart;
    private String selectedProductName;

    public void initialize(AppiumDriver driver, FrameworkConfig config) {
        catalog = new CatalogScreen(driver, config);
        productDetails = new ProductDetailsScreen(driver, config);
        cart = new CartScreen(driver, config);
    }

    public CatalogScreen catalog() {
        return requireInitialized(catalog);
    }

    public ProductDetailsScreen productDetails() {
        return requireInitialized(productDetails);
    }

    public CartScreen cart() {
        return requireInitialized(cart);
    }

    public String selectedProductName() {
        return requireInitialized(selectedProductName);
    }

    public void selectedProductName(String productName) {
        selectedProductName = productName;
    }

    private static <T> T requireInitialized(T value) {
        if (value == null) {
            throw new IllegalStateException("The device scenario has not been initialized");
        }
        return value;
    }
}
