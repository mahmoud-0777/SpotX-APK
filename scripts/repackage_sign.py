#!/usr/bin/env python3
"""Rebuild and sign patched APK."""

from __future__ import annotations

import argparse
import subprocess
from pathlib import Path


def run_cmd(cmd: list[str]) -> None:
    subprocess.run(cmd, check=True)


def build_apk(apktool_bin: str, workspace: Path, unsigned_output: Path) -> None:
    unsigned_output.parent.mkdir(parents=True, exist_ok=True)
    run_cmd([apktool_bin, "b", str(workspace), "-o", str(unsigned_output)])


def align_apk(zipalign_bin: str, unsigned_apk: Path, aligned_apk: Path) -> None:
    run_cmd([zipalign_bin, "-f", "4", str(unsigned_apk), str(aligned_apk)])


def sign_apk(apksigner_bin: str, aligned_apk: Path, keystore: Path, alias: str, key_pass: str) -> None:
    run_cmd(
        [
            apksigner_bin,
            "sign",
            "--ks",
            str(keystore),
            "--ks-key-alias",
            alias,
            "--ks-pass",
            f"pass:{key_pass}",
            str(aligned_apk),
        ]
    )


def main() -> None:
    parser = argparse.ArgumentParser(description="Rebuild and sign patched Spotify APK")
    parser.add_argument("--workspace", type=Path, default=Path("workspace/decompiled_apk"))
    parser.add_argument("--unsigned-apk", type=Path, default=Path("workspace/output/spotify-patched-unsigned.apk"))
    parser.add_argument("--aligned-apk", type=Path, default=Path("workspace/output/spotify-patched-aligned.apk"))
    parser.add_argument("--apktool", default="apktool")
    parser.add_argument("--zipalign", default="zipalign")
    parser.add_argument("--apksigner", default="apksigner")
    parser.add_argument("--keystore", type=Path, required=True)
    parser.add_argument("--alias", required=True)
    parser.add_argument("--key-pass", required=True)

    args = parser.parse_args()

    build_apk(args.apktool, args.workspace.resolve(), args.unsigned_apk.resolve())
    align_apk(args.zipalign, args.unsigned_apk.resolve(), args.aligned_apk.resolve())
    sign_apk(
        args.apksigner,
        args.aligned_apk.resolve(),
        args.keystore.resolve(),
        args.alias,
        args.key_pass,
    )


if __name__ == "__main__":
    main()
