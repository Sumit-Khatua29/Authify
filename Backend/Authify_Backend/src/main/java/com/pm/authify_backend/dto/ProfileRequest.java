package com.pm.authify_backend.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {

    @NotBlank
    private String name;
    @Email(message = "Enter valid email address")
    @NotNull(message = "Email should not be Empty")
    private String email;
    @Size(min = 6,message = "Password length must be atleast 6 characters")
    private String password;
}
