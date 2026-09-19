@foundation
Feature: Framework startup
  The automation runtime must fail early when a contributor uses an unsupported toolchain.

  Scenario: Start the framework with the supported Java runtime
    Given the automation framework is running on Java 17
    Then the test process uses UTF-8 encoding
