package com.example.monolithbank.service;

import com.example.monolithbank.dto.JwtResponse;
import com.example.monolithbank.dto.LoginRequest;
import com.example.monolithbank.dto.SignUpRequest;
import com.example.monolithbank.dto.TokenRefreshRequest;

public interface AuthService {
    JwtResponse authenticate(LoginRequest loginRequest);
    String register(SignUpRequest signUpRequest);
    JwtResponse refreshToken(TokenRefreshRequest request);
}
