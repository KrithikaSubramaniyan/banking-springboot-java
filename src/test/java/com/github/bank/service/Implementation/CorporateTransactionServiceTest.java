package com.github.bank.service.Implementation;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.TransactionDTO;
import com.github.bank.model.Entity.CorporateAccount;
import com.github.bank.model.Entity.Transaction;
import com.github.bank.repository.AccountRepository;
import com.github.bank.repository.CorporateAccountRepository;
import com.github.bank.repository.TransactionRepository;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.Currency;
import com.github.bank.util.Enum.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CorporateTransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CorporateAccountRepository corporateAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CorporateTransactionService corporateTransactionService;

    private CorporateAccount corporateAccount;
    private Transaction transaction;
    private TransactionDTO transactionDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        corporateAccount = new CorporateAccount();
        corporateAccount.setId(1L);
        corporateAccount.setAccountStatus(AccountStatus.Active);
        corporateAccount.setAccountBalance(new BigDecimal(500000));
        corporateAccount.setCurrency(Currency.USD);

        transaction = new Transaction();
        transaction.setAccountId(corporateAccount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionAmount(new BigDecimal(100000));

        transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionAmount(new BigDecimal(100000));
        transactionDTO.setTransactionType(TransactionType.DEPOSIT);
    }

    @Test
    public void testDepositAmount_Success() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(corporateAccount));
        when(corporateAccountRepository.save(corporateAccount)).thenReturn(corporateAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        BigDecimal depositAmount = new BigDecimal(100000);
        BigDecimal newBalance = corporateTransactionService.depositAmount(1L, depositAmount);

        assertEquals(new BigDecimal(600000), newBalance);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(corporateAccountRepository, times(1)).save(corporateAccount);
    }

    @Test
    public void testDepositAmount_InvalidAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            corporateTransactionService.depositAmount(1L, new BigDecimal(-100));
        });
        assertEquals("Deposit amount must be greater than zero.", exception.getMessage());
    }

    @Test
    public void testDepositAmount_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            corporateTransactionService.depositAmount(1L, new BigDecimal(100000));
        });
        assertEquals("Account not found with ID 1", exception.getMessage());
    }

    @Test
    public void testWithdrawAmount_Success() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(corporateAccount));
        when(transactionRepository.getTotalWithdrawalsForToday(1L, LocalDateTime.now().with(LocalTime.MIN))).thenReturn(new BigDecimal(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(corporateAccountRepository.save(corporateAccount)).thenReturn(corporateAccount);

        BigDecimal withdrawalAmount = new BigDecimal(100000);

        BigDecimal newBalance = corporateTransactionService.withdrawAmount(1L, withdrawalAmount);

        assertEquals(new BigDecimal(400000), newBalance);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(corporateAccountRepository, times(1)).save(corporateAccount);
    }

    @Test
    public void testWithdrawAmount_InsufficientBalance() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(corporateAccount));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            corporateTransactionService.withdrawAmount(1L, new BigDecimal(1000000));
        });
        assertEquals("Insufficient balance.", exception.getMessage());
    }

    @Test
    public void testWithdrawAmount_DailyLimitExceeded() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(corporateAccount));
        when(transactionRepository.getTotalWithdrawalsForToday(1L, LocalDateTime.now().with(LocalTime.MIN)))
                .thenReturn(new BigDecimal(9900000));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(corporateAccountRepository.save(corporateAccount)).thenReturn(corporateAccount);

        BigDecimal withdrawalAmount = new BigDecimal(200000);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            corporateTransactionService.withdrawAmount(1L, withdrawalAmount);
        });
        assertEquals("Daily withdrawal limit exceeded (10_000K USD)", exception.getMessage());
    }

    @Test
    public void testGetTransactions() {
        when(corporateAccountRepository.findById(1L)).thenReturn(java.util.Optional.of(corporateAccount));
        when(transactionRepository.findByAccountId(corporateAccount)).thenReturn(Arrays.asList(transaction));

        List<TransactionDTO> transactions = corporateTransactionService.getTransactions(1L);

        assertEquals(1, transactions.size());
        assertEquals(transactionDTO.getTransactionAmount(), transactions.get(0).getTransactionAmount());
        Assertions.assertEquals(transactionDTO.getTransactionType(), transactions.get(0).getTransactionType());
    }
}
