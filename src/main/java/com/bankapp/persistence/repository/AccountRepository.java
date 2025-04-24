package com.bankapp.persistence.repository;

import com.bankapp.persistence.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity,Long> {
    Optional<AccountEntity>findByUsername(String username);
}
