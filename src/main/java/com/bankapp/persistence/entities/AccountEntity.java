package com.bankapp.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
public class AccountEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_account")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = false, length = 200)
    private String password;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Role role;
    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = Role.ROLE_USER;
        }
    }
    @Transient // Este campo no se persiste en la base de datos
    private Collection<? extends GrantedAuthority> authorities;

    @OneToMany(mappedBy = "account")
    private List<TransactionEntity> transactions;

    public AccountEntity(String username, String password, BigDecimal balance, List<TransactionEntity> transactions, Role role) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.transactions = transactions;
        this.role = role;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = List.of(new SimpleGrantedAuthority(role.name()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
