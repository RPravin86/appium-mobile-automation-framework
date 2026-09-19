#!/usr/bin/env bash
set -euo pipefail

requested_platform="${1:-all}"
project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
application_directory="${project_root}/test-apps"

download() {
    local url="$1"
    local destination="$2"

    if [[ -s "${destination}" ]]; then
        echo "Using existing ${destination}"
        return
    fi

    local temporary_file
    temporary_file="$(mktemp "${destination}.download.XXXXXX")"
    trap 'rm -f "${temporary_file}"' RETURN
    curl --fail --location --retry 3 --output "${temporary_file}" "${url}"
    mv "${temporary_file}" "${destination}"
    trap - RETURN
    echo "Downloaded ${destination}"
}

mkdir -p "${application_directory}"

case "${requested_platform}" in
    android)
        download \
            "https://github.com/saucelabs/my-demo-app-android/releases/download/2.2.0/mda-2.2.0-25.apk" \
            "${application_directory}/my-demo-app-android-2.2.0.apk"
        ;;
    ios)
        download \
            "https://github.com/saucelabs/my-demo-app-ios/releases/download/2.2.2/SauceLabs-Demo-App.ipa" \
            "${application_directory}/my-demo-app-ios-2.2.2.ipa"
        ;;
    all)
        "$0" android
        "$0" ios
        ;;
    *)
        echo "Usage: $0 [android|ios|all]" >&2
        exit 2
        ;;
esac
