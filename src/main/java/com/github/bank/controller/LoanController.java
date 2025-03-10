package com.github.bank.controller;

import com.github.bank.service.LoanCalculatorService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/loan")
@Slf4j
public class LoanController {
    private final LoanCalculatorService loanCalculatorService;

    @Autowired
    public LoanController(LoanCalculatorService loanCalculatorService) {
        this.loanCalculatorService = loanCalculatorService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> calculateLoan(
            @PathVariable @NotNull(message = "User ID cannot be null") Long userId,
            @RequestParam @NotNull(message = "Loan term cannot be null") int loanTerm) {
        BigDecimal loanAmount = loanCalculatorService.calculateLoanAmount(userId, loanTerm);
        String message = "The customer is eligible for the loan amount of " + loanAmount;
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
