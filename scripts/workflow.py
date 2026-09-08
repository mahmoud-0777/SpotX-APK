#!/usr/bin/env python3
"""End-to-end SpotX-APK workflow: decompile, patch, rebuild, sign."""

from __future__ import annotations

import argparse
import subprocess
from pathlib import Path


def run_step(title: str, command: list[str]) -> None:
    print(f"\n==> {title}")
    subprocess.run(command, check=True)


def main() -> None:
    parser = argparse.ArgumentParser(description="Run full SpotX-APK patch workflow")
    parser.add_argument("apk", type=Path, help="Input Spotify APK path")
    parser.add_argument("--profile", type=Path, default=Path("config/profiles/default.json"))
    parser.add_argument("--workspace", type=Path, default=Path("workspace/decompiled_apk"))
    parser.add_argument("--keystore", type=Path, required=True)
    parser.add_argument("--alias", required=True)
    parser.add_argument("--key-pass", required=True)

    args = parser.parse_args()

    run_step(
        "Decompiling APK",
        [
            "python3",
            "scripts/decompile.py",
            str(args.apk),
            "--output",
            str(args.workspace),
        ],
    )
    run_step(
        "Applying smali patches",
        [
            "python3",
            "scripts/patch_engine.py",
            "--workspace",
            str(args.workspace),
            "--profile",
            str(args.profile),
        ],
    )
    run_step(
        "Rebuilding and signing APK",
        [
            "python3",
            "scripts/repackage_sign.py",
            "--workspace",
            str(args.workspace),
            "--keystore",
            str(args.keystore),
            "--alias",
            args.alias,
            "--key-pass",
            args.key_pass,
        ],
    )


if __name__ == "__main__":
    main()
