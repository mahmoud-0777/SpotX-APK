# Usage Guide

## Android app workflow

1. Install and open the SpotX APK Patcher app.
2. Tap **Select Spotify APK** and choose an APK file from storage.
3. Choose profile (**Full**, **Premium**, or **Minimal**) or toggle patches manually.
4. Tap **Patch APK** and wait for completion notification.
5. Retrieve patched APK from `Android/data/com.spotx.apk/files/PatchedApks`.

## 1) Decompile only

```bash
python3 scripts/decompile.py /path/to/spotify.apk
```

## 2) Apply patches only

```bash
python3 scripts/patch_engine.py --profile config/profiles/default.json
```

## 3) Rebuild and sign only

```bash
python3 scripts/repackage_sign.py \
  --keystore /path/to/keystore.jks \
  --alias your_alias \
  --key-pass your_password
```

## 4) Full automated workflow

```bash
bash scripts/patch_apk.sh \
  /path/to/spotify.apk \
  /path/to/keystore.jks \
  your_alias \
  your_password \
  config/profiles/default.json
```
