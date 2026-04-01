package com.example.monolithbank.repository;

import com.example.monolithbank.domain.Role;
import com.example.monolithbank.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);

    boolean existsByName(RoleName roleName);
}
