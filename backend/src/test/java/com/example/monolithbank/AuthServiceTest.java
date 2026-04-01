package com.example.monolithbank;

import com.example.monolithbank.domain.Role;
import com.example.monolithbank.domain.RoleName;
import com.example.monolithbank.dto.JwtResponse;
import com.example.monolithbank.dto.LoginRequest;
import com.example.monolithbank.dto.SignUpRequest;
import com.example.monolithbank.repository.RoleRepository;
import com.example.monolithbank.repository.UserRepository;
import com.example.monolithbank.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(new Role(roleName));
            }
        }
    }

    @Test
    void shouldRegisterAndLoginUser() {
        SignUpRequest signup = new SignUpRequest();
        signup.setUsername("testuser");
        signup.setEmail("testuser@example.com");
        signup.setPassword("password123");
        signup.setRoles(Set.of("customer"));

        String response = authService.register(signup);
        assertThat(response).contains("successfully");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        JwtResponse jwtResponse = authService.authenticate(loginRequest);
        assertThat(jwtResponse.getAccessToken()).isNotBlank();
        assertThat(jwtResponse.getRefreshToken()).isNotBlank();
        assertThat(jwtResponse.getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldFailDuplicateUsername() {
        SignUpRequest signup = new SignUpRequest();
        signup.setUsername("userdup");
        signup.setEmail("userdup@example.com");
        signup.setPassword("password123");
        signup.setRoles(Set.of("customer"));
        authService.register(signup);

        SignUpRequest signup2 = new SignUpRequest();
        signup2.setUsername("userdup");
        signup2.setEmail("another@example.com");
        signup2.setPassword("password123");
        signup2.setRoles(Set.of("customer"));

        assertThrows(Exception.class, () -> authService.register(signup2));
    }
}
