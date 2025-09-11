package com.udacity.jwdnd.course1.cloudstorage.DTOs;

import jakarta.validation.constraints.*;


public record SignupRequest(
        @NotBlank @Size(min = 3, max = 20) String username,
        @NotBlank @Size(min = 8,max = 72) String password,
        @NotBlank @Size(max = 20) String firstName,
        @NotBlank @Size(max = 20) String lastname

){}