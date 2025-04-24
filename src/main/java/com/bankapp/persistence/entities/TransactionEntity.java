package com.bankapp.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction",nullable = false)
    private Long idTransaction;
    private BigDecimal amount;
    private String type;
    private LocalDateTime timestamp;

    @JoinColumn(name = "id_account")
    @ManyToOne
    private AccountEntity account;
}
