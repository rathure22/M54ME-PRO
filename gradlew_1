#!/bin/sh
# M54ME PRO gradlew - 8.5 ready - dual mode
set -e
DIR=$(dirname "$0")
JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
if [ -f "$JAR" ]; then
  exec java -jar "$JAR" "$@"
else
  echo "[M54ME] Wrapper jar not found, using system gradle 8.5..."
  if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
  else
    echo "Installing gradle 8.5..."
    cd /tmp
    wget -q https://services.gradle.org/distributions/gradle-8.5-bin.zip
    unzip -q -o gradle-8.5-bin.zip
    export PATH=/tmp/gradle-8.5/bin:$PATH
    cd "$DIR"
    exec /tmp/gradle-8.5/bin/gradle "$@"
  fi
fi
