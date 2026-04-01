package com.example.monolithbank.repository;

import com.example.monolithbank.domain.RefreshToken;
import com.example.monolithbank.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
