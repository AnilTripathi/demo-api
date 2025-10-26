package com.myhealth.service;

import com.myhealth.dto.RegisterRequest;
import com.myhealth.dto.RegisterResponse;

public interface UserRegistrationService {
    RegisterResponse registerUser(RegisterRequest registerRequest);
}