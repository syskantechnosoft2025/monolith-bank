package com.example.monolithbank.controller;

import com.example.monolithbank.domain.Account;
import com.example.monolithbank.domain.AccountType;
import com.example.monolithbank.domain.User;
import com.example.monolithbank.dto.ApiResponse;
import com.example.monolithbank.repository.UserRepository;
import com.example.monolithbank.security.UserPrincipal;
import com.example.monolithbank.service.AccountService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    @Autowired
    public AccountController(AccountService accountService, UserRepository userRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Account> createAccount(@AuthenticationPrincipal UserPrincipal principal,
                                                 @RequestParam AccountType accountType,
                                                 @RequestParam @NotNull BigDecimal initialDeposit) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Account account = accountService.createAccount(user, accountType, initialDeposit);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getMyAccounts(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(accountService.getAccountsByUser(user));
    }
}
