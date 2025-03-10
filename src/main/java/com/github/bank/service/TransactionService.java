package com.github.bank.service;

import com.github.bank.model.DTO.TransactionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    BigDecimal depositAmount(Long accountId, BigDecimal transactionAmount);

    BigDecimal withdrawAmount(Long accountId, BigDecimal transactionAmount);

    List<TransactionDTO> getTransactions(Long accountId);
}
