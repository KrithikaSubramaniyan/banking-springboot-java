package com.github.bank.controller;

import com.github.bank.service.Implementation.IndividualUserTransactionService;
import com.github.bank.service.Implementation.CorporateTransactionService;
import com.github.bank.util.Enum.UserType;
import com.github.bank.model.DTO.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private IndividualUserTransactionService individualUserTransactionService;

    @Mock
    private CorporateTransactionService corporateTransactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        reset(individualUserTransactionService, corporateTransactionService);
    }

    @Test
    void testDepositAmount() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("1000.00");
        UserType userType = UserType.Person;
        BigDecimal newBalance = new BigDecimal("5000.00");

        when(individualUserTransactionService.depositAmount(accountId, amount)).thenReturn(newBalance);

        ResponseEntity<String> response = transactionController.depositAmount(accountId, amount, userType);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Updated balance after deposit is " + newBalance, response.getBody());
        verify(individualUserTransactionService, times(1)).depositAmount(accountId, amount);
    }

    @Test
    void testWithdrawAmount() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("500.00");
        UserType userType = UserType.Person;
        BigDecimal newBalance = new BigDecimal("4500.00");

        when(individualUserTransactionService.withdrawAmount(accountId, amount)).thenReturn(newBalance);

        ResponseEntity<String> response = transactionController.withdrawAmount(accountId, amount, userType);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Updated balance after withdrawal is " + newBalance, response.getBody());
        verify(individualUserTransactionService, times(1)).withdrawAmount(accountId, amount);
    }

    @Test
    void testGetTransactions() {
        Long accountId = 1L;
        UserType userType = UserType.Person;
        TransactionDTO dto1 = mock(TransactionDTO.class);
        TransactionDTO dto2 = mock(TransactionDTO.class);
        List<TransactionDTO> mockTransactions = Arrays.asList(dto1, dto2);

        when(individualUserTransactionService.getTransactions(accountId)).thenReturn(mockTransactions);

        ResponseEntity<List<TransactionDTO>> response = transactionController.getTransactions(accountId, userType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(individualUserTransactionService, times(1)).getTransactions(accountId);
    }

    @Test
    void testDepositAmountCorporate() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("5000.00");
        UserType userType = UserType.Corporate;
        BigDecimal newBalance = new BigDecimal("15000.00");

        when(corporateTransactionService.depositAmount(accountId, amount)).thenReturn(newBalance);

        ResponseEntity<String> response = transactionController.depositAmount(accountId, amount, userType);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Updated balance after deposit is " + newBalance, response.getBody());
        verify(corporateTransactionService, times(1)).depositAmount(accountId, amount);
    }

    @Test
    void testWithdrawAmountCorporate() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("1000.00");
        UserType userType = UserType.Corporate;
        BigDecimal newBalance = new BigDecimal("9000.00");

        when(corporateTransactionService.withdrawAmount(accountId, amount)).thenReturn(newBalance);

        ResponseEntity<String> response = transactionController.withdrawAmount(accountId, amount, userType);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Updated balance after withdrawal is " + newBalance, response.getBody());
        verify(corporateTransactionService, times(1)).withdrawAmount(accountId, amount);
    }

    @Test
    void testGetTransactionsCorporate() {
        Long accountId = 1L;
        UserType userType = UserType.Corporate;
        TransactionDTO dto1 = mock(TransactionDTO.class);
        TransactionDTO dto2 = mock(TransactionDTO.class);
        List<TransactionDTO> mockTransactions = Arrays.asList(dto1, dto2);

        when(corporateTransactionService.getTransactions(accountId)).thenReturn(mockTransactions);

        ResponseEntity<List<TransactionDTO>> response = transactionController.getTransactions(accountId, userType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(corporateTransactionService, times(1)).getTransactions(accountId);
    }
}
