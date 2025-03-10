package com.github.bank.service.Implementation;

import com.github.bank.model.Mapper.TransactionToTransactionDTO;
import com.github.bank.repository.AccountRepository;
import com.github.bank.repository.IndividualAccountRepository;
import com.github.bank.repository.TransactionRepository;
import com.github.bank.service.TransactionService;
import com.github.bank.util.AgeValidator;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.TransactionType;
import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.TransactionDTO;
import com.github.bank.model.Entity.Account;
import com.github.bank.model.Entity.IndividualAccount;
import com.github.bank.model.Entity.IndividualUser;
import com.github.bank.model.Entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndividualUserTransactionService implements TransactionService {
    // TODO: check if it is 10000 or 10000K
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal(10_000_000);
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT_MINOR = new BigDecimal(1_000_000);
    private final AccountRepository accountRepository;
    private final IndividualAccountRepository individualAccountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public IndividualUserTransactionService(AccountRepository accountRepository,
                                            IndividualAccountRepository individualAccountRepository,
                                            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.individualAccountRepository = individualAccountRepository;
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
        IndividualAccount individualAccount = (IndividualAccount) account;

        if (!AccountStatus.Active.equals(individualAccount.getAccountStatus())) {
            throw new IllegalStateException("Account is not active.");
        }
        Transaction transaction = new Transaction();
        transaction.setAccountId(account);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionAmount(transactionAmount);
        BigDecimal newBalance = individualAccount.getAccountBalance().add(transactionAmount);
        individualAccount.setAccountBalance(newBalance);

        individualAccountRepository.save(individualAccount);
        transactionRepository.save(transaction);
        return newBalance;
    }

    @Transactional
    @Override
    public BigDecimal withdrawAmount(Long accountId, BigDecimal transactionAmount) {

        Transaction transaction = new Transaction();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID " + accountId));
        IndividualAccount individualAccount = (IndividualAccount) account;
        if (!AccountStatus.Active.equals(individualAccount.getAccountStatus())) {
            throw new IllegalStateException("Account is not active.");
        }
        int balance = individualAccount.getAccountBalance().compareTo(transactionAmount);
        if (individualAccount.getAccountBalance() == null || balance <= 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        BigDecimal totalWithdrawnToday = transactionRepository.getTotalWithdrawalsForToday(accountId, startOfDay);
        IndividualUser user = individualAccount.getUser();
        if (AgeValidator.isMinor(user.getDateOfBirth())) {
            if (totalWithdrawnToday.add(transactionAmount).compareTo(DAILY_WITHDRAWAL_LIMIT_MINOR) > 0) {
                throw new IllegalArgumentException("Daily withdrawal limit exceeded (1_000K " + individualAccount.getCurrency() + ")");
            }
        } else if (totalWithdrawnToday.add(transactionAmount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new IllegalArgumentException("Daily withdrawal limit exceeded (10_000K " + individualAccount.getCurrency() + ")");
        }
        transaction.setAccountId(account);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setTransactionAmount(transactionAmount);
        BigDecimal newBalance = individualAccount.getAccountBalance().subtract(transactionAmount);
        individualAccount.setAccountBalance(newBalance);

        individualAccountRepository.save(individualAccount);
        transactionRepository.save(transaction);
        return newBalance;
    }

    @Override
    public List<TransactionDTO> getTransactions(Long accountId) {
        TransactionToTransactionDTO transactionToTransactionDTO = new TransactionToTransactionDTO();
        Account account = individualAccountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException("Account not found with ID " + accountId));
        List<Transaction> transactions = transactionRepository.findByAccountId(account);
        return transactions.stream()
                .map(transactionToTransactionDTO::convert)
                .collect(Collectors.toList());
    }
}
