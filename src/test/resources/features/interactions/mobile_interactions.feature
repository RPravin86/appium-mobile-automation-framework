@foundation
Feature: Cross-platform mobile interactions
  Screen objects should use one interaction contract while retaining platform-specific locators and gestures.

  Scenario Outline: Resolve the locator for the active mobile platform
    Given a platform locator with Android id "android-login" and iOS id "ios-login"
    When the locator is resolved for "<platform>"
    Then the resolved locator contains "<expectedId>"

    Examples:
      | platform | expectedId    |
      | android  | android-login |
      | ios      | ios-login     |

  Scenario: Build an Android swipe inside safe viewport bounds
    Given a mobile viewport that is 1000 by 2000 pixels
    When an Android up swipe command is built for 0.75 percent
    Then the UiAutomator2 swipe uses the safe viewport bounds

  Scenario: Reject an invalid swipe distance
    Given a mobile viewport that is 1000 by 2000 pixels
    Then a swipe percentage above one is rejected
