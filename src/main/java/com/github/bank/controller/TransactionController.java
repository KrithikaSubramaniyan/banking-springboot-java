package com.github.bank.controller;

import com.github.bank.service.Implementation.IndividualUserTransactionService;
import com.github.bank.service.Implementation.CorporateTransactionService;
import com.github.bank.util.Enum.UserType;
import com.github.bank.model.DTO.TransactionDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final IndividualUserTransactionService individualUserTransactionService;
    private final CorporateTransactionService corporateTransactionService;

    @Autowired
    public TransactionController(IndividualUserTransactionService individualUserTransactionService, CorporateTransactionService corporateTransactionService) {
        this.individualUserTransactionService = individualUserTransactionService;
        this.corporateTransactionService = corporateTransactionService;
    }

    @PostMapping("/deposit-amount/{accountId}")
    public ResponseEntity<String> depositAmount(
            @PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
            @RequestParam @NotNull(message = "Deposit amount cannot be null") BigDecimal amount,
            @RequestParam @NotNull(message = "User Type(Corporate/Person) cannot be null") UserType userType) {
        BigDecimal newBalance;
        if (UserType.Corporate.equals(userType)) {
            newBalance = corporateTransactionService.depositAmount(accountId, amount);
        } else {
            newBalance = individualUserTransactionService.depositAmount(accountId, amount);
        }
        String message = "Updated balance after deposit is " + newBalance;
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw-amount/{accountId}")
    public ResponseEntity<String> withdrawAmount(
            @PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
            @RequestParam @NotNull(message = "Withdrawal amount cannot be null") BigDecimal amount,
            @RequestParam @NotNull(message = "User Type(Corporate/Person) cannot be null") UserType userType) {

        BigDecimal newBalance;
        if (UserType.Corporate.equals(userType)) {
            newBalance = corporateTransactionService.withdrawAmount(accountId, amount);
        } else {
            newBalance = individualUserTransactionService.withdrawAmount(accountId, amount);
        }
        String message = "Updated balance after withdrawal is " + newBalance;
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactions(
            @PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
            @RequestParam @NotNull(message = "User Type(Corporate/Person) cannot be null") UserType userType) {
        List<TransactionDTO> transactionDTOs;
        if (UserType.Corporate.equals(userType)) {
            transactionDTOs = corporateTransactionService.getTransactions(accountId);
        } else {
            transactionDTOs = individualUserTransactionService.getTransactions(accountId);
        }
        return new ResponseEntity<>(transactionDTOs, HttpStatus.OK);
    }
}
