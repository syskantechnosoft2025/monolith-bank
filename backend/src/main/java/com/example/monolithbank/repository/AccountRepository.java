package com.example.monolithbank.repository;

import com.example.monolithbank.domain.Account;
import com.example.monolithbank.domain.AccountType;
import com.example.monolithbank.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByOwner(User owner);
    List<Account> findByType(AccountType type);
    @Query("select a from Account a where a.owner.username = :username")
    List<Account> findByOwnerUsername(@Param("username") String username);
}
