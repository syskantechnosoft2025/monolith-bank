package com.example.monolithbank.service;

import com.example.monolithbank.domain.RefreshToken;
import com.example.monolithbank.domain.Role;
import com.example.monolithbank.domain.RoleName;
import com.example.monolithbank.domain.User;
import com.example.monolithbank.dto.JwtResponse;
import com.example.monolithbank.dto.LoginRequest;
import com.example.monolithbank.dto.SignUpRequest;
import com.example.monolithbank.dto.TokenRefreshRequest;
import com.example.monolithbank.exception.BadRequestException;
import com.example.monolithbank.repository.RoleRepository;
import com.example.monolithbank.repository.UserRepository;
import com.example.monolithbank.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                           JwtProvider jwtProvider, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String accessToken = jwtProvider.generateAccessToken(user.getUsername(), roles);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new JwtResponse(accessToken, refreshToken.getToken(), user.getId(), user.getUsername(), user.getEmail(), roles);
    }

    @Override
    public String register(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Customer role not found"));
            roles.add(customerRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equalsIgnoreCase("admin")) {
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Admin role not found"));
                    roles.add(adminRole);
                } else if (role.equalsIgnoreCase("manager")) {
                    Role managerRole = roleRepository.findByName(RoleName.ROLE_MANAGER)
                            .orElseThrow(() -> new RuntimeException("Manager role not found"));
                    roles.add(managerRole);
                } else {
                    Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                            .orElseThrow(() -> new RuntimeException("Customer role not found"));
                    roles.add(customerRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public JwtResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        List<String> roles = user.getRoles().stream().map(Role::getName).map(Enum::name).collect(Collectors.toList());
        String accessToken = jwtProvider.generateAccessToken(user.getUsername(), roles);
        return new JwtResponse(accessToken, refreshToken.getToken(), user.getId(), user.getUsername(), user.getEmail(), roles);
    }
}
