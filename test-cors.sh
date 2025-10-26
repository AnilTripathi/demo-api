#!/bin/bash

# CORS Testing Script for MyHealth API
# This script demonstrates how to test CORS configuration

echo "=== CORS Configuration Testing Script ==="
echo "This script tests the CORS configuration of the MyHealth API"
echo "Make sure the application is running on http://localhost:8080"
echo ""

# Test 1: OPTIONS preflight request
echo "1. Testing OPTIONS preflight request..."
curl -v -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,Authorization" \
  2>&1 | grep -E "(Access-Control|HTTP/)"

echo ""
echo "Expected headers:"
echo "  Access-Control-Allow-Origin: http://localhost:3000"
echo "  Access-Control-Allow-Methods: GET,POST,PUT,DELETE,PATCH,OPTIONS,HEAD"
echo "  Access-Control-Allow-Headers: *"
echo "  Access-Control-Allow-Credentials: true"
echo "  Access-Control-Max-Age: 3600"
echo ""

# Test 2: Actual cross-origin request
echo "2. Testing actual cross-origin GET request..."
curl -v -X GET http://localhost:8080/v3/api-docs \
  -H "Origin: http://example.com" \
  2>&1 | grep -E "(Access-Control|HTTP/)"

echo ""
echo "Expected headers:"
echo "  Access-Control-Allow-Origin: http://example.com"
echo "  Access-Control-Allow-Credentials: true"
echo ""

# Test 3: Different origin
echo "3. Testing with different origin..."
curl -v -X OPTIONS http://localhost:8080/api/auth/register \
  -H "Origin: https://myapp.com" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  2>&1 | grep -E "(Access-Control|HTTP/)"

echo ""
echo "Expected headers:"
echo "  Access-Control-Allow-Origin: https://myapp.com"
echo ""

echo "=== CORS Testing Complete ==="
echo ""
echo "To test from a browser:"
echo "1. Open browser console on http://localhost:3000"
echo "2. Run: fetch('http://localhost:8080/v3/api-docs', {method: 'GET'})"
echo "3. Check Network tab for CORS headers"
echo ""
echo "Note: This configuration allows ALL origins and is for DEVELOPMENT ONLY!"
echo "For production, restrict origins to specific domains."