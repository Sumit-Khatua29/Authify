package com.pm.authify_backend.controller;


import com.pm.authify_backend.dto.ProfileRequest;
import com.pm.authify_backend.dto.ProfileResponse;
import com.pm.authify_backend.service.EmailService;
import com.pm.authify_backend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController

public class ProfileController {

    @Autowired
    private  ProfileService profileService;

    @Autowired
    private EmailService emailService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest profileRequest){
        ProfileResponse response = profileService.createProfile(profileRequest);
        emailService.sendWelcomeEmail(response.getEmail(), response.getName());
        return response;
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email) {
       return profileService.getProfile(email);
    }
}
