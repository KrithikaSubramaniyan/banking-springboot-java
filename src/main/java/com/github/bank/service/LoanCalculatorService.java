package com.github.bank.service;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.repository.IndividualAccountRepository;
import com.github.bank.repository.JointAccountRepository;
import com.github.bank.repository.TransactionRepository;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.AccountType;
import com.github.bank.util.Enum.TransactionType;
import com.github.bank.model.Entity.IndividualAccount;
import com.github.bank.model.Entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanCalculatorService {

    private final IndividualAccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final JointAccountRepository jointAccountRepository;

    @Autowired
    public LoanCalculatorService(IndividualAccountRepository accountRepository, TransactionRepository transactionRepository,
                                 JointAccountRepository jointAccountRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.jointAccountRepository = jointAccountRepository;
    }

    public BigDecimal calculateLoanAmount(Long userId, int loanTerm) {

        if (loanTerm < 24 || loanTerm > 72 || loanTerm % 3 != 0) {
            throw new IllegalArgumentException("Loan term must be between 24 and 72 months and in increments of 3 months.");
        }

        // Fetch the list of all accounts for the given client
        List<IndividualAccount> accounts = accountRepository.findByUserId(userId);
        List<Long> jointAccounts = jointAccountRepository.getJointAccounts(userId);
        jointAccounts.stream().forEach(jointAccount -> {
            IndividualAccount account = accountRepository.findById(jointAccount)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
            accounts.add(account);
        });
        List<IndividualAccount> activeAccounts = accounts.stream()
                .filter(
                        individualAccount -> individualAccount.getAccountStatus().equals(AccountStatus.Active)
                        && !individualAccount.getAccountType().equals(AccountType.Junior)
//                        && !individualAccount.getAccountHolderType().equals(AccountHolderType.Joint)
                ).toList();
        BigDecimal totalMonthlySurplus = BigDecimal.ZERO;

        for (IndividualAccount account : activeAccounts) {
            BigDecimal averageMonthlySurplus = calculateAverageMonthlySurplus(account);
            totalMonthlySurplus = totalMonthlySurplus.add(averageMonthlySurplus);
        }

        return totalMonthlySurplus.multiply(BigDecimal.valueOf(loanTerm));
    }

    BigDecimal calculateAverageMonthlySurplus(IndividualAccount account) {
        LocalDate oneYearAgo = LocalDate.now().minusMonths(12);

        List<Transaction> transactions = transactionRepository.findByAccountAndDateBetween(account.getId(), oneYearAgo);

        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (TransactionType.DEPOSIT.equals(transaction.getTransactionType())) {
                totalDeposits = totalDeposits.add(transaction.getTransactionAmount());
            } else if (TransactionType.WITHDRAWAL.equals(transaction.getTransactionType())) {
                totalWithdrawals = totalWithdrawals.add(transaction.getTransactionAmount());
            }
        }

        BigDecimal averageMonthlySurplus = totalDeposits.subtract(totalWithdrawals)
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        return averageMonthlySurplus;
    }
}
