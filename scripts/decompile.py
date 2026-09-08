#!/usr/bin/env python3
"""Decompile APK using apktool."""

from __future__ import annotations

import argparse
import subprocess
from pathlib import Path


def run_decompile(apk_path: Path, output_dir: Path, apktool_bin: str) -> None:
    if not apk_path.is_file():
        raise FileNotFoundError(f"APK file not found: {apk_path}")

    output_dir.parent.mkdir(parents=True, exist_ok=True)
    cmd = [apktool_bin, "d", str(apk_path), "-f", "-o", str(output_dir)]
    subprocess.run(cmd, check=True)


def main() -> None:
    parser = argparse.ArgumentParser(description="Decompile APK into smali workspace")
    parser.add_argument("apk", type=Path, help="Path to Spotify APK")
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("workspace/decompiled_apk"),
        help="Output directory for decompiled APK",
    )
    parser.add_argument("--apktool", default="apktool", help="apktool executable name/path")

    args = parser.parse_args()
    run_decompile(args.apk.resolve(), args.output.resolve(), args.apktool)


if __name__ == "__main__":
    main()
