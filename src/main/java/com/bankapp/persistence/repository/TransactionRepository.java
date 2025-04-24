package com.bankapp.persistence.repository;

import com.bankapp.persistence.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
    List<TransactionEntity>findByAccountId(Long idAccount);
}
