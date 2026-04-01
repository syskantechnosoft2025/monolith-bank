package com.example.monolithbank.service;

import com.example.monolithbank.domain.RefreshToken;
import com.example.monolithbank.domain.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUserId(Long userId);
    RefreshToken findByToken(String token);
}
