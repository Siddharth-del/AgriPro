#!/bin/bash
set -e

echo "=========================================="
echo " Starting Flask ML Service..."
echo "=========================================="
cd /ml
python3 app.py &
FLASK_PID=$!

echo "Waiting for Flask to initialize..."
sleep 10

# Check Flask actually started
if ! kill -0 $FLASK_PID 2>/dev/null; then
    echo "[ERROR] Flask failed to start. Exiting."
    exit 1
fi

echo "=========================================="
echo " Starting Spring Boot..."
echo "=========================================="
cd /app
exec java -Xmx256m -jar app.jar