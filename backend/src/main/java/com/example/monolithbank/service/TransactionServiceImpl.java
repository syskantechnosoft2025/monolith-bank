package com.example.monolithbank.service;

import com.example.monolithbank.domain.Account;
import com.example.monolithbank.domain.Transaction;
import com.example.monolithbank.domain.TransactionType;
import com.example.monolithbank.exception.BadRequestException;
import com.example.monolithbank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final JavaMailSender mailSender;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountService accountService, JavaMailSender mailSender) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.mailSender = mailSender;
    }

    @Override
    public Transaction createTransaction(Account account, TransactionType type, BigDecimal amount, boolean debit, String remarks) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        if (debit && account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        BigDecimal newBalance = debit ? account.getBalance().subtract(amount) : account.getBalance().add(amount);
        account.setBalance(newBalance);

        // Persist updated account balance before saving transaction
        accountService.updateAccount(account);

        Transaction transaction = new Transaction(account, type, amount, debit, LocalDateTime.now(), remarks);
        Transaction saved = transactionRepository.save(transaction);

        sendTransactionalEmail(account.getOwner().getEmail(), type, amount, account.getAccountNumber());

        return saved;
    }

    @Override
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        Account account = accountService.findByNumber(accountNumber);
        return createTransaction(account, TransactionType.DEPOSIT, amount, false, "Deposit");
    }

    @Override
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountService.findByNumber(accountNumber);
        return createTransaction(account, TransactionType.WITHDRAW, amount, true, "Withdraw");
    }

    @Override
    public Transaction transfer(String fromAccount, String toAccount, BigDecimal amount) {
        if (fromAccount.equals(toAccount)) {
            throw new BadRequestException("Cannot transfer to same account");
        }
        Account source = accountService.findByNumber(fromAccount);
        Account destination = accountService.findByNumber(toAccount);

        createTransaction(source, TransactionType.TRANSFER, amount, true, "Transfer to " + toAccount);
        Transaction transactionDest = createTransaction(destination, TransactionType.TRANSFER, amount, false, "Transfer from " + fromAccount);

        return transactionDest;
    }

    @Override
    public List<Transaction> searchTransactions(LocalDateTime start, LocalDateTime end, TransactionType type, Boolean debit) {
        return transactionRepository.search(start, end, type, debit);
    }

    private void sendTransactionalEmail(String to, TransactionType type, BigDecimal amount, String accountNumber) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Bank transaction notification");
            message.setText("Transaction " + type + " of amount " + amount + " on account " + accountNumber + " completed.");
            mailSender.send(message);
        } catch (Exception ignored) {
            // Log in real world
        }
    }
}
