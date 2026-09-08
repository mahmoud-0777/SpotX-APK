#!/usr/bin/env python3
"""Apply configured smali patches to decompiled APK files."""

from __future__ import annotations

import argparse
import json
from pathlib import Path


def load_json(path: Path) -> dict:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def matching_files(workspace: Path, target: str) -> list[Path]:
    if target.startswith("**/"):
        target = target[3:]
    return [p for p in workspace.rglob(target) if p.is_file()]


def apply_patch_to_file(file_path: Path, replacements: list[dict]) -> int:
    content = file_path.read_text(encoding="utf-8", errors="ignore")
    original = content
    file_replacements = 0

    for replacement in replacements:
        match = replacement["match"]
        replace = replacement["replace"]
        count = content.count(match)
        if count:
            content = content.replace(match, replace)
            file_replacements += count

    if content != original:
        file_path.write_text(content, encoding="utf-8")

    return file_replacements


def run_patch_engine(workspace: Path, patches_file: Path, profile_file: Path) -> dict[str, int]:
    patches_data = load_json(patches_file)
    profile_data = load_json(profile_file)

    enabled = set(profile_data.get("enabled_patches", []))
    results: dict[str, int] = {}

    for patch in patches_data.get("patches", []):
        patch_id = patch["id"]
        if patch_id not in enabled:
            continue

        total = 0
        targets = patch.get("targets", ["**/*.smali"])
        for target in targets:
            for file_path in matching_files(workspace, target):
                total += apply_patch_to_file(file_path, patch.get("replacements", []))

        results[patch_id] = total

    return results


def main() -> None:
    parser = argparse.ArgumentParser(description="Apply SpotX-APK smali patches")
    parser.add_argument(
        "--workspace",
        type=Path,
        default=Path("workspace/decompiled_apk"),
        help="Path to decompiled APK workspace",
    )
    parser.add_argument(
        "--patches",
        type=Path,
        default=Path("patches/patches.json"),
        help="Patch definition JSON file",
    )
    parser.add_argument(
        "--profile",
        type=Path,
        default=Path("config/profiles/default.json"),
        help="Patch profile JSON file",
    )

    args = parser.parse_args()
    results = run_patch_engine(args.workspace.resolve(), args.patches.resolve(), args.profile.resolve())

    for patch_id, applied in results.items():
        print(f"{patch_id}: {applied} replacements")


if __name__ == "__main__":
    main()
