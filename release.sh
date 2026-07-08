#!/usr/bin/env bash
#
# Local release helper for bootiful-sqltemplate.
#
# Turns the current -SNAPSHOT into a release version, commits and tags it, pushes
# the tag (which triggers the "Publish release" GitHub Actions workflow that
# builds, signs and uploads to the Maven Central Portal), then bumps to the next
# -SNAPSHOT.
#
# CI only deploys, so it needs no write access to the repository: this script
# owns the version/commit/tag/push. After the workflow uploads the bundle you
# still publish it manually at https://central.sonatype.com (autoPublish=false).
#
# Usage:
#   ./release.sh                 # e.g. 4.0.0-SNAPSHOT -> release 4.0.0, next 4.0.1-SNAPSHOT
#   ./release.sh 4.1.0-SNAPSHOT  # ... choosing the next development version explicitly
#
set -euo pipefail
cd "$(dirname "$0")"

# Ignore untracked files (e.g. local notes); only tracked changes must be clean.
if [ -n "$(git status --porcelain --untracked-files=no)" ]; then
    echo "error: working tree has uncommitted changes. Commit or stash them first." >&2
    exit 1
fi

set_version() {
    ./mvnw -q versions:set -DnewVersion="$1" -DprocessAllModules -DgenerateBackupPoms=false
}

CURRENT="$(./mvnw -q --non-recursive help:evaluate -Dexpression=project.version -DforceStdout)"
case "$CURRENT" in
    *-SNAPSHOT) ;;
    *) echo "error: current version ($CURRENT) is not a -SNAPSHOT version." >&2; exit 1 ;;
esac

RELEASE="${CURRENT%-SNAPSHOT}"

if [ $# -ge 1 ]; then
    NEXT="$1"
    case "$NEXT" in *-SNAPSHOT) ;; *) NEXT="${NEXT}-SNAPSHOT" ;; esac
else
    # default: bump the patch component (4.0.0 -> 4.0.1-SNAPSHOT)
    NEXT="${RELEASE%.*}.$(( ${RELEASE##*.} + 1 ))-SNAPSHOT"
fi

echo "Release version:      $CURRENT -> $RELEASE"
echo "Next development:     $NEXT"
echo "Tag to push:          $RELEASE (triggers the release workflow)"
read -r -p "Proceed? [y/N] " ans
case "$ans" in y|Y) ;; *) echo "aborted."; exit 1 ;; esac

# 1) release version, commit, tag
set_version "$RELEASE"
git commit -aqm "$RELEASE release"
git tag "$RELEASE"

# 2) next development version, commit
set_version "$NEXT"
git commit -aqm "start $NEXT"

# 3) push branch commits, then the tag (the tag push starts the release workflow)
git push
git push origin "$RELEASE"

cat <<EOF

Done. Pushed tag $RELEASE.
The "Publish release" workflow will build, sign and upload $RELEASE to the Central Portal.
Finish by publishing it manually at https://central.sonatype.com (autoPublish=false).
EOF
