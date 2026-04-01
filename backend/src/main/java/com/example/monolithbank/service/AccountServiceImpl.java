package com.example.monolithbank.service;

import com.example.monolithbank.domain.Account;
import com.example.monolithbank.domain.AccountType;
import com.example.monolithbank.domain.User;
import com.example.monolithbank.exception.BadRequestException;
import com.example.monolithbank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(User user, AccountType type, BigDecimal deposit) {
        if (deposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Deposit must be non-negative");
        }

        Account account = new Account();
        account.setOwner(user);
        account.setType(type);
        account.setBalance(deposit);
        account.setAccountNumber("AC" + System.currentTimeMillis());
        account.setCreatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByOwner(user);
    }

    @Override
    public Account findByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }
}
