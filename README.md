# Appium Mobile Automation Framework

Production-oriented Java and Cucumber automation for native Android and iOS applications. The framework is being delivered as a sequence of reviewable modules.

## Current module

`06-ci-execution` adds repeatable execution for GitHub Actions, Azure Pipelines, and Jenkins. Hosted agents run the device-independent verification suite; explicitly labelled self-hosted agents run Android devices, iOS simulators, or physical iPhones and publish the same reports and evidence.

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

Element lookup uses an explicit wait configured with `interaction.wait.seconds` and `interaction.poll.millis`. Screen objects inherit these settings through `BaseScreen`; individual tests should not add sleeps or create their own wait policies.

## Interaction boundary

`MobileActions` owns element lookup, tapping, text entry, visibility checks, and disappearance waits. Each operation records elapsed time at `DEBUG` level, which gives us useful performance diagnostics without cluttering normal execution logs.

`PlatformLocator` keeps Android and iOS selectors together at the screen-object boundary. `MobileGestures` translates a shared swipe direction into the correct Appium command: `mobile: swipeGesture` for UiAutomator2 and `mobile: swipe` for XCUITest. Gesture distances are validated before a device command is sent.

Foundation and interaction scenarios are device-independent and run with the normal suite:

```bash
mvn clean test
```

The generated Extent report is available at `reports/extent/index.html`. Cucumber JSON and JUnit XML remain under `target/cucumber-reports` for CI systems that consume machine-readable results.

## Evidence and recording policy

Failure screenshots are saved under `reports/evidence/screenshots` and embedded directly in the failed Extent scenario. Every `@device` scenario is recorded when `evidence.video.enabled=true`. The recording is saved under `reports/evidence/videos` and linked from the scenario in the report.

FFmpeg compresses recordings using H.264, CRF 32, a maximum width of 720 pixels, and no audio. These defaults keep videos readable while substantially reducing artifact size. Adjust them per run when needed:

```bash
mvn test \
  -Devidence.video.crf=30 \
  -Devidence.video.max.width=1080 \
  -Devidence.video.keep.raw=true
```

Install and verify FFmpeg before device execution:

```bash
brew install ffmpeg
ffmpeg -version
```

Recording or compression failures are treated as evidence warnings rather than product-test failures. When compression fails, the raw MP4 is retained for diagnosis.

## Run the commerce scenario

Real-device scenarios use the `@device` tag and are excluded from the default build. This keeps compilation and architectural checks useful on laptops and CI workers that do not have a device allocated.

Start Appium 3 with the appropriate driver, connect a device, and download the pinned sample application. For Android:

```bash
appium driver install uiautomator2
./scripts/download-sample-apps.sh android
adb devices

mvn clean test \
  -Dcucumber.filter.tags="@device" \
  -Dframework.platform=android \
  -Ddevice.name="Android Device" \
  -Ddevice.udid="<device-udid>" \
  -Dapp.path="test-apps/my-demo-app-android-2.2.0.apk"
```

For a physical iPhone, install the XCUITest driver and provide the Apple signing values required to build WebDriverAgent:

```bash
appium driver install xcuitest
./scripts/download-sample-apps.sh ios-device

mvn clean test \
  -Dcucumber.filter.tags="@device" \
  -Dframework.platform=ios \
  -Ddevice.name="iPhone" \
  -Ddevice.udid="<device-udid>" \
  -Dapp.path="test-apps/my-demo-app-ios-device-2.2.2.ipa" \
  -Dios.xcode.org.id="<apple-team-id>" \
  -Dios.xcode.signing.id="Apple Development" \
  -Dios.updated.wda.bundle.id="<unique-wda-bundle-id>"
```

The application may need to be re-signed for the target iPhone. Device-cloud application references will be handled by the CI execution module rather than treated as local filesystem paths.

For an Xcode Simulator, use the simulator build and omit all signing properties:

```bash
./scripts/download-sample-apps.sh ios-simulator

mvn clean test \
  -Dcucumber.filter.tags="@device" \
  -Dframework.platform=ios \
  -Ddevice.name="iPhone 15 Pro" \
  -Ddevice.udid="<simulator-udid>" \
  -Dapp.path="test-apps/my-demo-app-ios-simulator-2.2.2.zip"
```

## CI execution

The default CI path runs `mvn clean test` on Java 17 without requiring Appium or a device. All three pipeline implementations publish Extent, Cucumber, Surefire, log, screenshot, and video output even when a test fails.

### GitHub Actions

`.github/workflows/ci.yml` runs automatically for `main`, numbered module branches, and pull requests. `.github/workflows/device-tests.yml` is manual because a workflow must not claim a shared physical device unexpectedly.

Register self-hosted runners with these labels:

- Android: `self-hosted`, `mobile`, `android`
- iOS: `self-hosted`, `mobile`, `macOS`, `ios`

Each device runner requires Java 17, Maven, Appium, FFmpeg, `curl`, and its platform tooling. Install UiAutomator2 on Android agents and XCUITest on macOS agents. Physical-iPhone runs also use the repository secrets `IOS_XCODE_ORG_ID` and `IOS_UPDATED_WDA_BUNDLE_ID`.

### Azure Pipelines

`azure-pipelines.yml` runs verification on `ubuntu-latest`. Set `runDeviceTests=true` only after creating the self-hosted pool named by `mobileAgentPool`. Supply `FRAMEWORK_PLATFORM`, `DEVICE_NAME`, `DEVICE_UDID`, and `IOS_TARGET` as pipeline variables; store Apple signing values as secret variables.

### Jenkins

The `Jenkinsfile` always runs verification and exposes an optional device stage. Set `DEVICE_AGENT_LABEL` to the agent holding the allocated device. Apple signing values should be injected as protected environment variables or Jenkins credentials on the iOS agent.

### Shared device entry point

All device pipelines call the same checked-in script, which prevents CI implementations from drifting apart:

```bash
FRAMEWORK_PLATFORM=android \
DEVICE_NAME="Android Device" \
DEVICE_UDID="<device-udid>" \
START_APPIUM=true \
./scripts/run-device-tests.sh
```

For iOS, set `FRAMEWORK_PLATFORM=ios` and `IOS_TARGET=simulator` or `device`. The script validates required values, downloads the correct pinned application when absent, optionally manages the Appium process, and shuts down only the server process it started.

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
