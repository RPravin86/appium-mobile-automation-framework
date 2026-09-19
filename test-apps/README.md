# Test applications

Application binaries are deliberately excluded from Git. Mobile binaries are large, change independently from the framework, and iOS packages may contain organization-specific signing information.

The commerce suite uses Sauce Labs' native My Demo App builds. Versions are pinned so UI changes do not silently alter a regression run:

- Android `2.2.0`
- iOS `2.2.2`

Download one or both applications:

```bash
./scripts/download-sample-apps.sh android
./scripts/download-sample-apps.sh ios-device
./scripts/download-sample-apps.sh ios-simulator
./scripts/download-sample-apps.sh all
```

Use `my-demo-app-ios-device-2.2.2.ipa` with a physical iPhone or device cloud. Depending on the target device, your team may need to re-sign it with an Apple provisioning profile that includes that device.

Use `my-demo-app-ios-simulator-2.2.2.zip` with an Xcode Simulator. The simulator build does not require an Apple development team or provisioning profile. The `ios` argument remains available as an alias for `ios-device` for backward compatibility.
