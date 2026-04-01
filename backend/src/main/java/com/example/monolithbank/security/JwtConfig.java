package com.example.monolithbank.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtConfig {
    private String secret;
    private Long accessTokenExpirationMs;
    private Long refreshTokenExpirationMs;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public Long getAccessTokenExpirationMs() { return accessTokenExpirationMs; }
    public void setAccessTokenExpirationMs(Long accessTokenExpirationMs) { this.accessTokenExpirationMs = accessTokenExpirationMs; }
    public Long getRefreshTokenExpirationMs() { return refreshTokenExpirationMs; }
    public void setRefreshTokenExpirationMs(Long refreshTokenExpirationMs) { this.refreshTokenExpirationMs = refreshTokenExpirationMs; }
}
