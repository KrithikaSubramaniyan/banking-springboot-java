package com.github.bank.model.DTO;

import com.github.bank.util.Enum.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private String accountNumber;
    private TransactionType transactionType;
    private BigDecimal transactionAmount;
    private LocalDateTime transactionTime;
}