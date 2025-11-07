#!/bin/bash

# Script to run smoke tests against an external application
# Usage: ./run-smoke-tests.sh [BASE_URL]

set -e

BASE_URL=${1:-"http://localhost:8089"}
HEALTH_ENDPOINT="$BASE_URL/actuator/health"
MAX_RETRIES=30
RETRY_INTERVAL=2

echo "🔍 Running smoke tests against: $BASE_URL"

# Function to check if application is ready
check_health() {
    curl -s -f "$HEALTH_ENDPOINT" > /dev/null 2>&1
}

# Wait for application to be ready
echo "⏳ Waiting for application to be ready..."
for i in $(seq 1 $MAX_RETRIES); do
    if check_health; then
        echo "✅ Application is ready!"
        break
    fi
    
    if [ $i -eq $MAX_RETRIES ]; then
        echo "❌ Application failed to start within $(($MAX_RETRIES * $RETRY_INTERVAL)) seconds"
        echo "   Health endpoint: $HEALTH_ENDPOINT"
        exit 1
    fi
    
    echo "   Attempt $i/$MAX_RETRIES - waiting ${RETRY_INTERVAL}s..."
    sleep $RETRY_INTERVAL
done

# Run smoke tests
echo "🧪 Running smoke tests..."
export APP_BASE_URL="$BASE_URL"

mvn test -Psmoke-tests -q

echo "✅ Smoke tests completed successfully!"