#!/bin/bash
# MoneyMoment AI — Build Script
# Usage: ./build.sh [clean|debug|release|install|run]

set -e

ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-arm64}"

export ANDROID_HOME
export JAVA_HOME

# Ensure Java 17 is used
if [ -f "$JAVA_HOME/bin/java" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "=== MoneyMoment AI Build ==="
echo "Java: $(java -version 2>&1 | head -1)"
echo "SDK:  $ANDROID_HOME"
echo ""

# Check for required SDK components
if [ ! -d "$ANDROID_HOME/platforms/android-34" ]; then
    echo "Error: Android SDK platform 34 not found."
    echo "Install with: sdkmanager 'platforms;android-34'"
    exit 1
fi

# Check architecture for aapt2 workaround
ARCH=$(uname -m)
if [ "$ARCH" = "aarch64" ] || [ "$ARCH" = "arm64" ]; then
    echo "⚠️  ARM64 architecture detected."
    echo "   Android SDK build-tools 34.0.0 contain x86_64 binaries."
    echo ""
    echo "Options:"
    echo "  1. Build on an x86_64 machine (recommended)"
    echo "  2. Use Android Studio on any architecture"
    echo "  3. Install qemu-user-static for ARM64 x86_64 emulation"
    echo ""
    echo "Attempting build with system aapt2 workaround..."
    echo ""
fi

case "${1:-debug}" in
    clean)
        echo "Cleaning..."
        ./gradlew clean
        ;;
    debug)
        echo "Building debug APK..."
        ./gradlew assembleDebug
        echo ""
        echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
        ;;
    release)
        echo "Building release APK..."
        ./gradlew assembleRelease
        echo ""
        echo "APK location: app/build/outputs/apk/release/app-release.apk"
        ;;
    install)
        echo "Installing debug APK..."
        ./gradlew installDebug
        ;;
    run)
        echo "Building and running..."
        ./gradlew installDebug
        if [ -f "$ANDROID_HOME/platform-tools/adb" ]; then
            $ANDROID_HOME/platform-tools/adb shell am start -n com.moneymoment.ai/.MainActivity
        fi
        ;;
    *)
        echo "Usage: $0 [clean|debug|release|install|run]"
        exit 1
        ;;
esac
