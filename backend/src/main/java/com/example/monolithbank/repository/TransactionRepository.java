package com.example.monolithbank.repository;

import com.example.monolithbank.domain.Transaction;
import com.example.monolithbank.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountAccountNumber(String accountNumber);
    List<Transaction> findByTransactionType(TransactionType type);

    @Query("select t from Transaction t where (:start is null or t.transactionDate >= :start) and (:end is null or t.transactionDate <= :end) and (:type is null or t.transactionType = :type) and (:debit is null or t.debit = :debit)")
    List<Transaction> search(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             @Param("type") TransactionType type,
                             @Param("debit") Boolean debit);
}
