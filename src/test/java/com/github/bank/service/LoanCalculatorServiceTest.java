package com.github.bank.service;

import com.github.bank.model.Entity.IndividualAccount;
import com.github.bank.model.Entity.Transaction;
import com.github.bank.repository.IndividualAccountRepository;
import com.github.bank.repository.TransactionRepository;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanCalculatorServiceTest {

    @Mock
    private IndividualAccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private LoanCalculatorService loanCalculatorService;

    private IndividualAccount activeAccount;
    private IndividualAccount inactiveAccount;

    private Transaction depositTransaction;
    private Transaction withdrawalTransaction;

    @BeforeEach
    public void setUp() {
        activeAccount = new IndividualAccount();
        activeAccount.setId(1L);
        activeAccount.setAccountStatus(AccountStatus.Active);

        inactiveAccount = new IndividualAccount();
        inactiveAccount.setId(2L);
        inactiveAccount.setAccountStatus(AccountStatus.Closed);

        depositTransaction = new Transaction();
        depositTransaction.setTransactionType(TransactionType.DEPOSIT);
        depositTransaction.setTransactionAmount(new BigDecimal("1000"));

        withdrawalTransaction = new Transaction();
        withdrawalTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawalTransaction.setTransactionAmount(new BigDecimal("500"));
    }

    @Test
    public void testCalculateLoanAmount() {
        when(accountRepository.findByUserId(1L)).thenReturn(Arrays.asList(activeAccount, inactiveAccount));

        when(transactionRepository.findByAccountAndDateBetween(eq(1L), any(LocalDate.class)))
                .thenReturn(Arrays.asList(depositTransaction, withdrawalTransaction));

        BigDecimal loanAmount = loanCalculatorService.calculateLoanAmount(1L, 24);

        BigDecimal expectedSurplus = (depositTransaction.getTransactionAmount()
                .subtract(withdrawalTransaction.getTransactionAmount()))
                .divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(24));

        assertEquals(expectedSurplus, loanAmount);
    }

    @Test
    public void testCalculateLoanAmountWithNoActiveAccount() {
        when(accountRepository.findByUserId(1L)).thenReturn(Arrays.asList(inactiveAccount));

        BigDecimal loanAmount = loanCalculatorService.calculateLoanAmount(1L, 24);
        assertEquals(BigDecimal.ZERO, loanAmount);
    }

    @Test
    public void testCalculateLoanAmountWithInvalidLoanTerm() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanCalculatorService.calculateLoanAmount(1L, 23);
        });
        assertEquals("Loan term must be between 24 and 72 months and in increments of 3 months."
                , exception.getMessage());
    }

    @Test
    public void testCalculateAverageMonthlySurplus() {
        when(transactionRepository.findByAccountAndDateBetween(eq(1L), any(LocalDate.class)))
                .thenReturn(Arrays.asList(depositTransaction, withdrawalTransaction));

        BigDecimal averageMonthlySurplus = loanCalculatorService.calculateAverageMonthlySurplus(activeAccount);
        BigDecimal expectedAverageSurplus = new BigDecimal("41.67");

        assertEquals(expectedAverageSurplus, averageMonthlySurplus);
    }
}
