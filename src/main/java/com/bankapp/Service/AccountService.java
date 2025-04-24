package com.bankapp.Service;

import com.bankapp.persistence.entities.AccountEntity;
import com.bankapp.persistence.entities.TransactionEntity;
import com.bankapp.persistence.repository.AccountRepository;
import com.bankapp.persistence.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public AccountEntity findAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public AccountEntity createAccount(String username,String password){
        if (accountRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("username already exists");
        }
        AccountEntity account=new AccountEntity();
        account.setUsername(username);
        String encodedPassword = passwordEncoder.encode(password);
        account.setPassword(encodedPassword);
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    public void deposit(AccountEntity account,BigDecimal amount){
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        TransactionEntity transaction= new TransactionEntity();
        transaction.setAccount(account);
        transaction.setType("Deposit");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    public void withDraw(AccountEntity account,BigDecimal amount){
        if (account.getBalance().compareTo(amount)<0){
            throw new RuntimeException("insufficinet funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        TransactionEntity transaction= new TransactionEntity();
        transaction.setAccount(account);
        transaction.setType("WithDrawal");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    public List<TransactionEntity> getAllTransactionsByAccountId(AccountEntity account){
        List<TransactionEntity> allTransactions=
                transactionRepository.findByAccountId(account.getId());
        return allTransactions;
    }

    public void transferAmount(AccountEntity accountFrom, String usernameTo, BigDecimal amount) {
        if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        AccountEntity accountTo = accountRepository.findByUsername(usernameTo)
                .orElseThrow(() -> new RuntimeException("Recipient account not found"));

        // Actualizar saldo de la cuenta de origen
        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountRepository.save(accountFrom);

        // Actualizar saldo de la cuenta de destino
        accountTo.setBalance(accountTo.getBalance().add(amount));
        accountRepository.save(accountTo);

        // Registrar transacción de débito (cuenta origen)
        TransactionEntity transactionDebit = new TransactionEntity();
        transactionDebit.setAccount(accountFrom);
        transactionDebit.setAmount(amount);
        transactionDebit.setType("Transfer out to " + usernameTo);
        transactionDebit.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transactionDebit);

        // Registrar transacción de crédito (cuenta destino)
        TransactionEntity transactionCredit = new TransactionEntity(); // Cambiar aquí para usar transactionCredit
        transactionCredit.setAccount(accountTo);
        transactionCredit.setAmount(amount);
        transactionCredit.setType("Transfer from " + accountFrom.getUsername());
        transactionCredit.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transactionCredit);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity account = findAccountByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("Username or Password not found");
        }
        return account;
    }
}


