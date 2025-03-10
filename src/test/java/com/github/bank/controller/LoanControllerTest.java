package com.github.bank.controller;

import com.github.bank.service.LoanCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanCalculatorService loanCalculatorService;

    @InjectMocks
    private LoanController loanController;

    @BeforeEach
    void setUp() {
        reset(loanCalculatorService);
    }

    @Test
    void testCalculateLoan() {
        Long userId = 1L;
        int loanTerm = 12;
        BigDecimal expectedLoanAmount = new BigDecimal("50000.00");

        when(loanCalculatorService.calculateLoanAmount(userId, loanTerm)).thenReturn(expectedLoanAmount);

        ResponseEntity<String> response = loanController.calculateLoan(userId, loanTerm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The customer is eligible for the loan amount of 50000.00", response.getBody());
        verify(loanCalculatorService, times(1)).calculateLoanAmount(userId, loanTerm);
    }
}
