# Test applications

Application binaries are deliberately excluded from Git. Mobile binaries are large, change independently from the framework, and iOS packages may contain organization-specific signing information.

The commerce suite uses Sauce Labs' native My Demo App builds. Versions are pinned so UI changes do not silently alter a regression run:

- Android `2.2.0`
- iOS `2.2.2`

Download one or both applications:

```bash
./scripts/download-sample-apps.sh android
./scripts/download-sample-apps.sh ios
./scripts/download-sample-apps.sh all
```

The iOS IPA is intended for a real device or device cloud. Depending on the target device, your team may need to re-sign it with an Apple provisioning profile that includes that device.
