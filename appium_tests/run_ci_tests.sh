#!/bin/bash

# 1. Start Appium server in background
nohup appium --port 4723 > appium.log 2>&1 &
APPIUM_PID=$!
echo "Started Appium server with PID $APPIUM_PID"

# Register cleanup function to run on exit (success or failure)
cleanup() {
  echo "Stopping Appium server (PID $APPIUM_PID)..."
  kill $APPIUM_PID || true
}
trap cleanup EXIT

# 2. Wait for Appium to start
i=0
while [ $i -lt 15 ]; do
  if python -c "import socket; s = socket.socket(); s.connect(('localhost', 4723))" 2>/dev/null; then
    echo "Appium server is active!"
    break
  fi
  echo "Waiting for Appium server..."
  sleep 2
  i=$((i + 1))
done

# 3. Install the pre-built APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 4. Run tests
python appium_tests/run_tests.py
