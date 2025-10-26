package com.myhealth.service;

import com.myhealth.dto.AuthRequest;
import com.myhealth.dto.AuthResponse;
import com.myhealth.dto.RefreshRequest;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest);
    AuthResponse refresh(RefreshRequest refreshRequest);
    void logout(String refreshToken);
}