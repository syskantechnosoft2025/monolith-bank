package com.example.monolithbank.service;

import com.example.monolithbank.domain.Account;
import com.example.monolithbank.domain.Transaction;
import com.example.monolithbank.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Account account, TransactionType type, BigDecimal amount, boolean debit, String remarks);
    Transaction deposit(String accountNumber, BigDecimal amount);
    Transaction withdraw(String accountNumber, BigDecimal amount);
    Transaction transfer(String fromAccount, String toAccount, BigDecimal amount);
    List<Transaction> searchTransactions(LocalDateTime start, LocalDateTime end, TransactionType type, Boolean debit);
}
