# Appium Mobile Automation Framework

Production-oriented Java and Cucumber automation for native Android and iOS applications. The framework is being delivered as a sequence of reviewable modules; this branch contains the project foundation only.

## Current module

`01-project-foundation` establishes the build contract and dependency baseline. Device sessions, application screens, evidence capture, and CI pipelines are intentionally added in later branches so each architectural layer can be reviewed independently.

## Prerequisites

- Java 17
- Maven 3.9 or later
- Git

Confirm the local toolchain:

```bash
java -version
mvn -version
```

## Run the foundation suite

```bash
mvn clean test
```

The foundation scenario does not need Appium or a connected device. It verifies the Java runtime and encoding used by the test process.

## BDD architecture

Feature files describe observable mobile behaviour in the language used by the product team. Step definitions translate that behaviour into calls to screen objects; they will not contain locators or direct Appium commands. Screen objects and the interaction layer remain ordinary Java so reusable automation logic does not become coupled to Gherkin wording.

```text
Feature → Step definition → Screen object → Mobile interaction → Appium driver
```

Cucumber runs through its JUnit Platform engine. JUnit is execution infrastructure here, not the test-authoring style: scenarios live in `.feature` files, and TestNG is not part of the project. PicoContainer provides scenario-scoped dependency injection without static shared state.

## Dependency policy

Dependencies are pinned deliberately. Appium Java Client and Selenium evolve together, and allowing Maven to choose unrelated versions is a common source of runtime failures. The Selenium, Cucumber, JUnit, and Log4j BOMs keep related components aligned, while Maven Enforcer rejects dependency convergence problems during the build.

The current baseline uses Appium Java Client `10.1.1` with Selenium `4.44.0`. Appium's `10.1.1` release requires Selenium `4.42.0` or newer and explicitly adds compatibility with Selenium `4.44.0`.

BDD execution uses Cucumber JVM `7.34.8` and JUnit Platform `6.1.3`.

## Configuration precedence

Runtime values are resolved in this order:

```text
JVM system property → environment variable → platform properties → safe default
```

For example, `device.udid` can be supplied as either `-Ddevice.udid=...` or `DEVICE_UDID`. Real-device execution requires an explicit UDID so the framework never selects whichever device happens to appear first.

The default files are `config/common.properties` plus either `config/android.properties` or `config/ios.properties`. Keep machine-specific values outside Git by using system properties, environment variables, or an ignored `*.local.properties` file.

## Repository roadmap

The framework is developed using stacked branches:

1. `01-project-foundation`
2. `02-driver-configuration`
3. `03-mobile-interactions`
4. `04-commerce-tests`
5. `05-reporting-evidence`
6. `06-ci-execution`
7. `07-documentation-release`

Each branch is created from the previous reviewed branch. Nothing is merged into `main` during the development cycle.
