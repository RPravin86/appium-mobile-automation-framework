package io.github.rpravin86.mobile.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.rpravin86.mobile.context.CommerceScenarioContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CommerceSteps {

    private final CommerceScenarioContext context;

    public CommerceSteps(CommerceScenarioContext context) {
        this.context = context;
    }

    @Given("the shopper is viewing the product catalog")
    public void verifyProductCatalog() {
        assertTrue(context.catalog().isLoaded(), "The product catalog did not become visible");
    }

    @When("the shopper adds the first product to the cart")
    public void addFirstProductToCart() {
        context.selectedProductName(context.catalog().openFirstProduct());
        context.productDetails().addCurrentProductToCart();
    }

    @When("the shopper opens the cart")
    public void openCart() {
        context.productDetails().openCart();
    }

    @Then("the selected product is displayed in the cart")
    public void verifySelectedProduct() {
        assertTrue(context.cart().isLoaded(), "The cart did not become visible");
        assertTrue(
                context.cart().containsProduct(context.selectedProductName()),
                () -> "The cart does not contain " + context.selectedProductName()
        );
    }
}
