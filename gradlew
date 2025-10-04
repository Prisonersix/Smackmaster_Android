#!/bin/sh
# Gradle start up script generated for SmackMaster Android project.

DIR="$(dirname "$0")"
GRADLE_WRAPPER_DIR="$DIR/gradle/wrapper"
JAVA_HOME="${JAVA_HOME:-}"

if [ -z "$JAVA_HOME" ]; then
  JAVA_CMD="java"
else
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

CLASSPATH="$GRADLE_WRAPPER_DIR/gradle-wrapper.jar:$GRADLE_WRAPPER_DIR/gradle-wrapper-shared.jar"

exec "$JAVA_CMD" \
  -Dorg.gradle.appname="gradlew" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain "$@"
