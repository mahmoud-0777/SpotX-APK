# SpotX-APK

SpotX-APK now includes an **installable Android app** that guides users through:

1. Select Spotify APK
2. Choose patch profile or custom patch set
3. Patch in background with notifications
4. Retrieve patched APK and review patch history

## Android app setup

Requirements:

- Android Studio Iguana+ (or Gradle/AGP equivalents)
- Android SDK 34
- Device/emulator running Android 8.0+

Build:

```bash
./gradlew :app:assembleDebug
```

Install the generated APK to your phone and open **SpotX APK Patcher**.

## Android app features

- APK picker (Storage Access Framework)
- APK metadata (name/package/version/size)
- Patch profiles: **Full**, **Premium**, **Minimal**
- Custom patch toggles:
  - Ad-blocking
  - Hide podcasts/episodes/audiobooks
  - Disable auto-updates
  - Analytics blocking
  - Experimental features
- Background patch worker with progress notifications
- Patched APK output in `Android/data/com.spotx.apk/files/PatchedApks`
- Patch history list in-app
- In-app help panel/tutorial

## Supported Spotify versions

See `/home/runner/work/SpotX-APK/SpotX-APK/docs/SUPPORTED_VERSIONS.md`

## Troubleshooting

See `/home/runner/work/SpotX-APK/SpotX-APK/docs/TROUBLESHOOTING.md`

## CLI patcher (existing)

The original Python CLI workflow remains available.

- Usage: `/home/runner/work/SpotX-APK/SpotX-APK/docs/USAGE.md`
- Feature details: `/home/runner/work/SpotX-APK/SpotX-APK/docs/FEATURES.md`
