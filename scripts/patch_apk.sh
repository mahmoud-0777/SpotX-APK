#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 4 ]]; then
  echo "Usage: $0 <spotify.apk> <keystore.jks> <alias> <key_pass> [profile.json]"
  exit 1
fi

APK_PATH="$1"
KEYSTORE="$2"
ALIAS="$3"
KEY_PASS="$4"
PROFILE="${5:-config/profiles/default.json}"

python3 scripts/workflow.py \
  "$APK_PATH" \
  --keystore "$KEYSTORE" \
  --alias "$ALIAS" \
  --key-pass "$KEY_PASS" \
  --profile "$PROFILE"
