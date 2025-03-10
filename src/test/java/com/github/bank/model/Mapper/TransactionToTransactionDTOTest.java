package com.github.bank.model.Mapper;

import com.github.bank.model.DTO.TransactionDTO;
import com.github.bank.model.Entity.IndividualAccount;
import com.github.bank.model.Entity.Transaction;
import com.github.bank.util.Enum.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionToTransactionDTOTest {

    private TransactionToTransactionDTO transactionToTransactionDTO;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transactionToTransactionDTO = new TransactionToTransactionDTO();

        transaction = new Transaction();
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setTransactionAmount(new BigDecimal("1000.50"));
        transaction.setTransactionType(TransactionType.DEPOSIT);

        IndividualAccount account = new IndividualAccount();
        account.setId(123L);
        transaction.setAccountId(account);
    }

    @Test
    void testConvert() {
        TransactionDTO transactionDTO = transactionToTransactionDTO.convert(transaction);

        assertNotNull(transactionDTO);
        assertEquals(transaction.getCreatedAt(), transactionDTO.getTransactionTime());
        assertEquals(transaction.getTransactionAmount(), transactionDTO.getTransactionAmount());
        Assertions.assertEquals(transaction.getTransactionType(), transactionDTO.getTransactionType());
        assertEquals(String.valueOf(transaction.getAccountId().getId()), transactionDTO.getAccountNumber());
    }
}