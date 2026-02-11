package com.pm.authify_backend.service;


import com.pm.authify_backend.Entity.User;
import com.pm.authify_backend.dto.ProfileRequest;
import com.pm.authify_backend.dto.ProfileResponse;
import com.pm.authify_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProfileServiceImpl implements ProfileService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    @SuppressWarnings("null")
    public ProfileResponse createProfile(ProfileRequest profileRequest) {
        User newProfile = convertToUserEntity(profileRequest);
        if (userRepository.existsByEmail(profileRequest.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }else {
            newProfile = userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
    }

    @Override
    public ProfileResponse getProfile(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found : " + email));
        return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendResetOtp(String email) {
        User existingEntity = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found : " + email));
        //Generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //calculate expiry time (current time + 15 min in millisecs)
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);

        //update the profile/user
        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpiryAt(expiryTime);

        //save into the database
        userRepository.save(existingEntity);

        try{
            emailService.sendResetOtpEmail(existingEntity.getEmail(), otp);
        }catch (Exception ex){
            throw  new RuntimeException("Unable to send email to " + email);
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
       User existingUser =  userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found :" + email));
       if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid Otp");
       }
       if (existingUser.getResetOtpExpiryAt() < System.currentTimeMillis()){
           throw new RuntimeException("Otp expired");
       }
       // update password
       existingUser.setPassword(passwordEncoder.encode(newPassword));
       existingUser.setResetOtp(null);
       existingUser.setResetOtpExpiryAt(0L);

       userRepository.save(existingUser);

    }

    @Override
    public void sendOtp(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found  : " + email));

        if (existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()){
            return;
        }
        //Generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //calculate expiry time (current time + 24 hrs in millisecs)
        long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        //update User
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpiryAt(expiryTime);

        //save to  database
        userRepository.save(existingUser);

        try {
            emailService.sendVerifyOtpEmail(existingUser.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send email to " + email);
        }

    }

    @Override
    public void verifyOtp(String email, String otp) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found :" + email));
        if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid Otp");
        }
        if (existingUser.getVerifyOtpExpiryAt() < System.currentTimeMillis()){
            throw new RuntimeException("Otp expired");
        }
        //update user
        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpiryAt(0L);
        userRepository.save(existingUser);
    }

    private ProfileResponse convertToProfileResponse(User newProfile) {
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .user_id(newProfile.getUser_id())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }

    private User convertToUserEntity(ProfileRequest profileRequest){
        return User.builder()
                .email(profileRequest.getEmail())
                .user_id(UUID.randomUUID().toString())
                .name(profileRequest.getName())
                .password(passwordEncoder.encode(profileRequest.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpiryAt(0L)
                .verifyOtp(null)
                .verifyOtpExpiryAt(0L)
                .resetOtp(null)
                .build();
    }
}
