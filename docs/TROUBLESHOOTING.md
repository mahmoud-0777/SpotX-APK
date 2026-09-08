# Troubleshooting

## "Patch APK" does nothing
- Make sure an APK was selected first.
- Ensure at least one patch is enabled.

## Output file not generated
- Confirm there is free storage space on the device.
- Check app notifications for patch worker status/errors.
- Check app external files directory: `Android/data/com.spotx.apk/files/PatchedApks`.

## Metadata shows Unknown package/version
- Some APKs expose limited archive metadata. Patching can still proceed.

## Repack/sign steps fallback to copy mode
- The app runs with fallback behavior if apktool/toolchain binaries are not installed in app files (`files/toolchain`).
- Add compatible binaries to `files/toolchain` to enable on-device decode/rebuild/sign commands.

## App fails on very large APK files
- Close background apps and retry.
- Use newer devices with more free memory/storage.
