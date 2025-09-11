package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.DTOs.SignupRequest;
import com.udacity.jwdnd.course1.cloudstorage.DTOs.SignupResponse;

public interface AuthService {
    SignupResponse signup(SignupRequest signupRequest);
}
