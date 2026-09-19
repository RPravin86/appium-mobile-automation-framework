#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${project_root}"

: "${FRAMEWORK_PLATFORM:?FRAMEWORK_PLATFORM must be android or ios}"
: "${DEVICE_UDID:?DEVICE_UDID must identify the allocated device or simulator}"

device_name="${DEVICE_NAME:-Mobile Device}"
start_appium="${START_APPIUM:-false}"
appium_pid=""

stop_managed_appium() {
    if [[ -n "${appium_pid}" ]] && kill -0 "${appium_pid}" 2>/dev/null; then
        kill "${appium_pid}"
        wait "${appium_pid}" 2>/dev/null || true
    fi
}
trap stop_managed_appium EXIT

if [[ "${start_appium}" == "true" ]]; then
    command -v appium >/dev/null || {
        echo "Appium is not installed or is not available in PATH" >&2
        exit 1
    }
    mkdir -p logs
    appium --address "${APPIUM_SERVER_HOST:-127.0.0.1}" --port "${APPIUM_SERVER_PORT:-4723}" \
        > logs/appium-server.log 2>&1 &
    appium_pid="$!"

    server_url="${APPIUM_SERVER_URL:-http://127.0.0.1:4723}"
    for attempt in {1..30}; do
        if curl --silent --fail "${server_url}/status" >/dev/null; then
            break
        fi
        if [[ "${attempt}" -eq 30 ]]; then
            echo "Appium did not become ready at ${server_url}" >&2
            exit 1
        fi
        sleep 1
    done
fi

case "${FRAMEWORK_PLATFORM}" in
    android)
        app_path="${APP_PATH:-test-apps/my-demo-app-android-2.2.0.apk}"
        [[ -s "${app_path}" ]] || ./scripts/download-sample-apps.sh android
        ;;
    ios)
        ios_target="${IOS_TARGET:-simulator}"
        case "${ios_target}" in
            simulator)
                app_path="${APP_PATH:-test-apps/my-demo-app-ios-simulator-2.2.2.zip}"
                [[ -s "${app_path}" ]] || ./scripts/download-sample-apps.sh ios-simulator
                ;;
            device)
                app_path="${APP_PATH:-test-apps/my-demo-app-ios-device-2.2.2.ipa}"
                [[ -s "${app_path}" ]] || ./scripts/download-sample-apps.sh ios-device
                ;;
            *)
                echo "IOS_TARGET must be simulator or device" >&2
                exit 2
                ;;
        esac
        ;;
    *)
        echo "FRAMEWORK_PLATFORM must be android or ios" >&2
        exit 2
        ;;
esac

maven_arguments=(
    --batch-mode
    --no-transfer-progress
    clean
    test
    "-Dcucumber.filter.tags=@device"
    "-Dframework.platform=${FRAMEWORK_PLATFORM}"
    "-Ddevice.name=${device_name}"
    "-Ddevice.udid=${DEVICE_UDID}"
    "-Dapp.path=${app_path}"
)

mvn "${maven_arguments[@]}"
