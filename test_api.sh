#!/bin/bash
set -x

EMAIL="testuser_$(date +%s)@example.com"
PASSWORD="password123"

echo "1. Registering user"
curl -s -X POST http://localhost:8080/api/profile/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Test User\",\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" > reg.out

echo "2. Logging in"
curl -s -v -X POST http://localhost:8080/api/profile/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" 2> login.err > login.out

COOKIE=$(grep "Set-Cookie" login.err | awk -F': ' '{print $2}' | awk -F';' '{print $1}')
echo "Cookie: $COOKIE"

echo "3. Send OTP"
curl -s -v -X POST http://localhost:8080/api/profile/send-otp \
  -H "Cookie: $COOKIE" 2> send.err > send.out

echo "4. Verify OTP (using invalid OTP just to test if it passes 401)"
curl -s -v -X POST http://localhost:8080/api/profile/verify-otp \
  -H "Cookie: $COOKIE" \
  -H "Content-Type: application/json" \
  -d '{"otp":"123456"}' 2> verify.err > verify.out

cat verify.err | grep HTTP/
echo "Done"
