package com.github.bank.service.Implementation;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.TransactionDTO;
import com.github.bank.model.Entity.IndividualAccount;
import com.github.bank.model.Entity.IndividualUser;
import com.github.bank.model.Entity.Transaction;
import com.github.bank.model.Mapper.TransactionToTransactionDTO;
import com.github.bank.repository.AccountRepository;
import com.github.bank.repository.IndividualAccountRepository;
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
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IndividualUserTransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IndividualAccountRepository individualAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private IndividualUserTransactionService individualUserTransactionService;

    private IndividualAccount individualAccount;
    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private IndividualUser individualUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        individualUser = new IndividualUser();
        LocalDateTime localDateTime = LocalDateTime.now().minusYears(25);
        individualUser.setDateOfBirth(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        individualAccount = new IndividualAccount();
        individualAccount.setId(1L);
        individualAccount.setAccountStatus(AccountStatus.Active);
        individualAccount.setAccountBalance(new BigDecimal(50000000));
        individualAccount.setCurrency(Currency.USD);
        individualAccount.setUser(individualUser);

        transaction = new Transaction();
        transaction.setAccountId(individualAccount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionAmount(new BigDecimal(100000));

        transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionAmount(new BigDecimal(100000));
        transactionDTO.setTransactionType(TransactionType.DEPOSIT);
    }

    @Test
    public void testDepositAmount_Success() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(individualAccount));
        when(individualAccountRepository.save(individualAccount)).thenReturn(individualAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        BigDecimal depositAmount = new BigDecimal(100000);
        BigDecimal newBalance = individualUserTransactionService.depositAmount(1L, depositAmount);

        assertEquals(new BigDecimal(50100000), newBalance);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(individualAccountRepository, times(1)).save(individualAccount);
    }

    @Test
    public void testDepositAmount_InvalidAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            individualUserTransactionService.depositAmount(1L, new BigDecimal(-100));
        });
        assertEquals("Deposit amount must be greater than zero.", exception.getMessage());
    }

    @Test
    public void testDepositAmount_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            individualUserTransactionService.depositAmount(1L, new BigDecimal(100000));
        });
        assertEquals("Account not found with ID 1", exception.getMessage());
    }

    @Test
    public void testWithdrawAmount_Success() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(individualAccount));
        when(transactionRepository.getTotalWithdrawalsForToday(1L, LocalDateTime.now().with(LocalTime.MIN))).thenReturn(new BigDecimal(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(individualAccountRepository.save(individualAccount)).thenReturn(individualAccount);

        BigDecimal withdrawalAmount = new BigDecimal(100000);

        BigDecimal newBalance = individualUserTransactionService.withdrawAmount(1L, withdrawalAmount);

        assertEquals(new BigDecimal(49900000), newBalance);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(individualAccountRepository, times(1)).save(individualAccount);
    }

    @Test
    public void testWithdrawAmount_InsufficientBalance() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(individualAccount));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            individualUserTransactionService.withdrawAmount(1L, new BigDecimal(60000000));
        });
        assertEquals("Insufficient balance.", exception.getMessage());
    }

    @Test
    public void testWithdrawAmount_DailyLimitExceeded_Adult() {
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(individualAccount));
        when(transactionRepository.getTotalWithdrawalsForToday(1L, LocalDateTime.now().with(LocalTime.MIN))).thenReturn(new BigDecimal(9000000));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(individualAccountRepository.save(individualAccount)).thenReturn(individualAccount);

        BigDecimal withdrawalAmount = new BigDecimal(20000000);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            individualUserTransactionService.withdrawAmount(1L, withdrawalAmount);
        });
        assertEquals("Daily withdrawal limit exceeded (10_000K USD)", exception.getMessage());
    }

    @Test
    public void testWithdrawAmount_DailyLimitExceeded_Minor() {
        LocalDateTime localDateTime = LocalDateTime.now().minusYears(16);
        individualUser.setDateOfBirth(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(individualAccount));
        when(transactionRepository.getTotalWithdrawalsForToday(1L, LocalDateTime.now().with(LocalTime.MIN))).thenReturn(new BigDecimal(900000));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(individualAccountRepository.save(individualAccount)).thenReturn(individualAccount);

        BigDecimal withdrawalAmount = new BigDecimal(200000);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            individualUserTransactionService.withdrawAmount(1L, withdrawalAmount);
        });
        assertEquals("Daily withdrawal limit exceeded (1_000K USD)", exception.getMessage());
    }

    @Test
    public void testGetTransactions() {
        when(individualAccountRepository.findById(1L)).thenReturn(java.util.Optional.of(individualAccount));
        when(transactionRepository.findByAccountId(individualAccount)).thenReturn(Arrays.asList(transaction));
        TransactionToTransactionDTO transactionToTransactionDTO = new TransactionToTransactionDTO();

        List<TransactionDTO> transactions = individualUserTransactionService.getTransactions(1L);

        assertEquals(1, transactions.size());
        assertEquals(transactionDTO.getTransactionAmount(), transactions.get(0).getTransactionAmount());
        Assertions.assertEquals(transactionDTO.getTransactionType(), transactions.get(0).getTransactionType());
    }
}
