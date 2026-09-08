#!/usr/bin/env sh

# This script is intended to run on Unix-like systems.

APP_NAME="gradlew"
APP_HOME=$(cd "$(dirname "$0")" && pwd)

JAVA_OPTS="-Xmx64m"

exec "$JAVA_HOME/bin/java" $JAVA_OPTS -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
