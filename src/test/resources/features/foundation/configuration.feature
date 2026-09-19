@foundation
Feature: Framework configuration
  Configuration must be predictable so a local run and a pipeline run select the same device intentionally.

  Scenario: Override a configuration file with JVM properties
    Given a valid Android configuration file
    And the device name is overridden with a JVM property
    When the framework configuration is loaded
    Then the overridden device name is used
    And the configured platform is Android

  Scenario: Reject an unsupported mobile platform
    Given a configuration file with platform "windows"
    When the framework configuration is loaded expecting a failure
    Then the configuration error contains "Unsupported mobile platform"
