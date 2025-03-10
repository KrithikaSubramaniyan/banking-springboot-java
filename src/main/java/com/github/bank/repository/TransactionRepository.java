package com.github.bank.repository;

import com.github.bank.model.Entity.Account;
import com.github.bank.model.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "SELECT COALESCE(SUM(t.transactionamount), 0) FROM transactions t " +
            "WHERE t.accountid = :accountId AND t.transactionType = 'WITHDRAWAL' " +
            "AND t.createdat >= :startOfDay", nativeQuery = true)
    BigDecimal getTotalWithdrawalsForToday(@Param("accountId") Long accountId, LocalDateTime startOfDay);

    @Query(value = "select * from transactions where accountid = :accountId and createdat > :oneYearAgo", nativeQuery = true)
    List<Transaction> findByAccountAndDateBetween(Long accountId, LocalDate oneYearAgo);

    List<Transaction> findByAccountId(Account account);
}
