package com.github.bank.service.Implementation;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.TransactionDTO;
import com.github.bank.model.Entity.Account;
import com.github.bank.model.Entity.CorporateAccount;
import com.github.bank.model.Entity.Transaction;
import com.github.bank.model.Mapper.TransactionToTransactionDTO;
import com.github.bank.repository.AccountRepository;
import com.github.bank.repository.CorporateAccountRepository;
import com.github.bank.repository.TransactionRepository;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.TransactionType;
import com.github.bank.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorporateTransactionService implements TransactionService {
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal(10_000_000);
    private final AccountRepository accountRepository;
    private final CorporateAccountRepository corporateAccountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public CorporateTransactionService(AccountRepository accountRepository,
                                       CorporateAccountRepository corporateAccountRepository,
                                       TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.corporateAccountRepository = corporateAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public BigDecimal depositAmount(Long accountId, BigDecimal transactionAmount) {
        if (transactionAmount == null || transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException("Account not found with ID " + accountId));

        CorporateAccount corporateAccount = (CorporateAccount) account;
        if (!AccountStatus.Active.equals(corporateAccount.getAccountStatus())) {
            throw new IllegalStateException("Account is not active.");
        }
        Transaction transaction = new Transaction();
        transaction.setAccountId(account);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionAmount(transactionAmount);
        BigDecimal newBalance = corporateAccount.getAccountBalance().add(transactionAmount);
        corporateAccount.setAccountBalance(newBalance);

        corporateAccountRepository.save(corporateAccount);
        transactionRepository.save(transaction);
        return newBalance;
    }

    @Transactional
    @Override
    public BigDecimal withdrawAmount(Long accountId, BigDecimal transactionAmount) {
        if (transactionAmount == null || transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID " + accountId));
        CorporateAccount corporateAccount = (CorporateAccount) account;
        if (!AccountStatus.Active.equals(corporateAccount.getAccountStatus())) {
            throw new IllegalStateException("Account is not active.");
        }
        int balance = corporateAccount.getAccountBalance().compareTo(transactionAmount);
        if (corporateAccount.getAccountBalance() == null || balance <= 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        BigDecimal totalWithdrawnToday = transactionRepository.getTotalWithdrawalsForToday(accountId, startOfDay);

        if (totalWithdrawnToday.add(transactionAmount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new IllegalArgumentException("Daily withdrawal limit exceeded (10_000K " + corporateAccount.getCurrency() + ")");
        }
        Transaction transaction = new Transaction();
        transaction.setAccountId(account);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setTransactionAmount(transactionAmount);
        BigDecimal newBalance = corporateAccount.getAccountBalance().subtract(transactionAmount);
        corporateAccount.setAccountBalance(newBalance);

        corporateAccountRepository.save(corporateAccount);
        transactionRepository.save(transaction);
        return newBalance;
    }

    @Override
    public List<TransactionDTO> getTransactions(Long accountId) {
        TransactionToTransactionDTO transactionToTransactionDTO = new TransactionToTransactionDTO();
        Account account = corporateAccountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException("Account not found with ID " + accountId));
        List<Transaction> transactions = transactionRepository.findByAccountId(account);
        return transactions.stream()
                .map(transactionToTransactionDTO::convert)
                .collect(Collectors.toList());
    }
}
