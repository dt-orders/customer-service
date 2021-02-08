#!/bin/bash

DATE="$(date +'%Y%m%d.%H%M')"
GIT_COMMIT_SHORT="$(git rev-parse --short HEAD)"
GIT_BRANCH="$(git branch --show-current)"
GIT_REMOTE_ORIGIN_URL="$(git config --get remote.origin.url)"

cat MANIFEST.template | \
    sed 's~MANIFEST_GIT_COMMIT_SHORT~'"$GIT_COMMIT_SHORT"'~' | \
    sed 's~MANIFEST_GIT_BRANCH~'"$GIT_BRANCH"'~' | \
    sed 's~MANIFEST_GIT_REMOTE_ORIGIN_URL~'"$GIT_REMOTE_ORIGIN_URL"'~' | \
    sed 's~MANIFEST_DATE~'"$DATE"'~' > target/MANIFEST
