package com.example.monolithbank;

import com.example.monolithbank.domain.Account;
import com.example.monolithbank.domain.AccountType;
import com.example.monolithbank.domain.Transaction;
import com.example.monolithbank.domain.User;
import com.example.monolithbank.repository.AccountRepository;
import com.example.monolithbank.repository.UserRepository;
import com.example.monolithbank.service.AccountService;
import com.example.monolithbank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = new User("tuser", "tuser@example.com", "$2a$10$Vj2Z1PIy5e5UBh.IpzdSPuKN/26uK1vZWz5cOIk6.9x8xgSFKXCNK");
        userRepository.save(user);
        account = accountService.createAccount(user, AccountType.SAVINGS, BigDecimal.valueOf(1000));
    }

    @Test
    void shouldDepositAndWithdraw() {
        Transaction deposit = transactionService.deposit(account.getAccountNumber(), BigDecimal.valueOf(500));
        assertThat(deposit).isNotNull();
        assertThat(deposit.isDebit()).isFalse();

        Transaction withdraw = transactionService.withdraw(account.getAccountNumber(), BigDecimal.valueOf(200));
        assertThat(withdraw).isNotNull();
        assertThat(withdraw.isDebit()).isTrue();

        Account updated = accountService.findByNumber(account.getAccountNumber());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1300));
    }
}
