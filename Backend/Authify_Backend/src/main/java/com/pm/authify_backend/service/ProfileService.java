package com.pm.authify_backend.service;

import com.pm.authify_backend.dto.ProfileRequest;
import com.pm.authify_backend.dto.ProfileResponse;

public interface ProfileService {
     ProfileResponse createProfile(ProfileRequest profileRequest);

     ProfileResponse getProfile(String email);

     void sendResetOtp(String email);

     void resetPassword(String email, String otp, String newPassword);

     void sendOtp(String email);

     void verifyOtp(String email, String otp);
}
