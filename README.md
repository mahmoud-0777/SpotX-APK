# SpotX-APK

SpotX-APK is an Android-focused Spotify APK patcher inspired by SpotX, built around **apktool decompilation + smali patching**.

## Project Structure

- `workspace/decompiled_apk/` - Decompiled APK workspace
- `patches/` - Patch definitions and smali patch templates
- `scripts/` - Automation scripts (decompile, patch, rebuild, sign)
- `docs/` - Setup, feature, and usage documentation
- `config/profiles/` - Patch profiles for different behaviors

## Requirements

- Python 3.10+
- Java (for apktool/signing tools)
- [apktool](https://ibotpeaches.github.io/Apktool/)
- Android build-tools (`zipalign`, `apksigner`)
- A keystore for signing patched APKs

Install Python requirements:

```bash
pip install -r requirements.txt
```

## Features

- Ad-blocking
- Hide podcasts/episodes/audiobooks
- Disable auto-updates
- Experimental feature modifications
- Analytics blocking

Feature details: `/home/runner/work/SpotX-APK/SpotX-APK/docs/FEATURES.md`

## Installation / Setup

1. Clone repository.
2. Install requirements listed above.
3. Prepare your Spotify APK input file.
4. Create or provide a signing keystore.
5. Choose a profile from `config/profiles/`.

## Usage

Quick workflow:

```bash
bash scripts/patch_apk.sh \
  /path/to/spotify.apk \
  /path/to/keystore.jks \
  your_alias \
  your_password
```

Detailed commands: `/home/runner/work/SpotX-APK/SpotX-APK/docs/USAGE.md`

## Patch Management

`patches/patches.json` controls patch groups and replacements. Profiles enable subsets of patch IDs:

- `config/profiles/default.json`
- `config/profiles/lite.json`

Adjust these files when Spotify APK internals change between versions.
