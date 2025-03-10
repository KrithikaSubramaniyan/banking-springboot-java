package com.github.bank.model.Mapper;

import com.github.bank.model.DTO.TransactionDTO;
import com.github.bank.model.Entity.Transaction;

public class TransactionToTransactionDTO {
    public TransactionDTO convert(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionTime(transaction.getCreatedAt());
        transactionDTO.setTransactionAmount(transaction.getTransactionAmount());
        transactionDTO.setTransactionType(transaction.getTransactionType());
        transactionDTO.setAccountNumber(String.valueOf(transaction.getAccountId().getId()));
        return transactionDTO;
    }
}
