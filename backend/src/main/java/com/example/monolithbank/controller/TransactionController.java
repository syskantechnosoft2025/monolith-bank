package com.example.monolithbank.controller;

import com.example.monolithbank.domain.Transaction;
import com.example.monolithbank.domain.TransactionType;
import com.example.monolithbank.dto.ApiResponse;
import com.example.monolithbank.service.TransactionService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Transaction> deposit(@RequestParam @NotBlank String accountNumber,
                                               @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(transactionService.deposit(accountNumber, amount));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Transaction> withdraw(@RequestParam @NotBlank String accountNumber,
                                                @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(transactionService.withdraw(accountNumber, amount));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Transaction> transfer(@RequestParam @NotBlank String fromAccount,
                                                @RequestParam @NotBlank String toAccount,
                                                @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(transactionService.transfer(fromAccount, toAccount, amount));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> search(@RequestParam(required = false) LocalDateTime start,
                                                    @RequestParam(required = false) LocalDateTime end,
                                                    @RequestParam(required = false) TransactionType type,
                                                    @RequestParam(required = false) Boolean debit) {
        return ResponseEntity.ok(transactionService.searchTransactions(start, end, type, debit));
    }

    @PostMapping("/apply-loan")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> applyLoan(@RequestParam @NotBlank String accountNumber,
                                               @RequestParam BigDecimal amount) {
        transactionService.deposit(accountNumber, amount);
        return ResponseEntity.ok(new ApiResponse(true, "Loan applied and amount credited"));
    }

    @PostMapping("/pay-emi")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> payLoan(@RequestParam @NotBlank String accountNumber,
                                               @RequestParam BigDecimal amount) {
        transactionService.withdraw(accountNumber, amount);
        return ResponseEntity.ok(new ApiResponse(true, "Loan EMI paid"));
    }
}
