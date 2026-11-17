#!/usr/bin/env bash
set -euo pipefail

bjoern_project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
exec "$bjoern_project_dir/gradlew" --project-dir "$bjoern_project_dir" \
    test buildPlugin verifyPluginProjectConfiguration verifyPluginStructure "$@"
