#!/bin/bash
EMAIL="test2_$(date +%s)@example.com"
curl -s -X POST http://localhost:8080/api/profile/register -H "Content-Type: application/json" -d "{\"name\":\"Test User\",\"email\":\"$EMAIL\",\"password\":\"password123\"}" > /dev/null
# Login and save headers
curl -s -D headers.txt -X POST http://localhost:8080/api/profile/login -H "Content-Type: application/json" -d "{\"email\":\"$EMAIL\",\"password\":\"password123\"}" > /dev/null
COOKIE=$(grep "Set-Cookie" headers.txt | awk -F': ' '{print $2}' | awk -F';' '{print $1}')
echo "Cookie: $COOKIE"

echo "Send OTP..."
curl -s -D send_headers.txt -X POST http://localhost:8080/api/profile/send-otp -H "Cookie: $COOKIE" > send.out
cat send_headers.txt | head -n 1
cat send.out

echo "Verify OTP..."
curl -s -D verify_headers.txt -X POST http://localhost:8080/api/profile/verify-otp -H "Cookie: $COOKIE" -H "Content-Type: application/json" -d '{"otp":"123456"}' > verify.out
cat verify_headers.txt | head -n 1
cat verify.out
