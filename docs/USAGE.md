# Usage Guide

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
