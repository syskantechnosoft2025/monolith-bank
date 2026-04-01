package com.example.monolithbank;

import com.example.monolithbank.domain.*;
import com.example.monolithbank.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepository, UserRepository userRepository,
                                        AccountRepository accountRepository, TransactionRepository transactionRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role(RoleName.ROLE_CUSTOMER));
                roleRepository.save(new Role(RoleName.ROLE_MANAGER));
                roleRepository.save(new Role(RoleName.ROLE_ADMIN));
            }

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("admin123"));
                Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).get();
                admin.setRoles(Set.of(adminRole));
                userRepository.save(admin);
            }

            if (!userRepository.existsByUsername("customer1")) {
                User customer = new User("customer1", "customer1@example.com", passwordEncoder.encode("password"));
                Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER).get();
                customer.setRoles(Set.of(customerRole));
                userRepository.save(customer);

                Account account1 = new Account("AC" + System.currentTimeMillis(), AccountType.SAVINGS, new BigDecimal("1000"), LocalDateTime.now(), customer);
                accountRepository.save(account1);

                transactionRepository.save(new Transaction(account1, TransactionType.DEPOSIT, new BigDecimal("1000"), false, LocalDateTime.now(), "Initial deposit"));
            }
        };
    }
}
