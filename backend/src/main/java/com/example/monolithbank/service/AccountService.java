package com.example.monolithbank.service;

import com.example.monolithbank.domain.Account;
import com.example.monolithbank.domain.AccountType;
import com.example.monolithbank.domain.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account createAccount(User user, AccountType type, BigDecimal deposit);
    List<Account> getAccountsByUser(User user);
    Account findByNumber(String accountNumber);

    Account approveLoan(String accountNumber);

    Account updateAccount(Account account);
}
