@device @commerce
Feature: Add a catalog product to the shopping cart
  A shopper should see the selected product in the cart before beginning checkout.

  Scenario: Add the first catalog product to the cart
    Given the shopper is viewing the product catalog
    When the shopper adds the first product to the cart
    And the shopper opens the cart
    Then the selected product is displayed in the cart
