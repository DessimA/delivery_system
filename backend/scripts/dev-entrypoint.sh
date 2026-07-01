#!/bin/sh
set -e

cd /app

echo "[dev] Building project..."
mvn compile -q

echo "[dev] Starting Spring Boot with DevTools..."
mvn spring-boot:run -q \
  -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.poll-interval=2s -Dspring.devtools.restart.quiet-period=1s" \
  &
SPRING_PID=$!

# Watch source files and recompile on change
echo "[dev] Watching for source changes..."
while true; do
  inotifywait -r -e modify,create,delete,move,move_self src/ 2>/dev/null
  echo "[dev] Source changed, recompiling..."
  mvn compile -q 2>&1 | grep -v "^$" || true
done
