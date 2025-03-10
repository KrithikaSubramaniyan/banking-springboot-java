package com.github.bank.repository;

import com.github.bank.util.Enum.AccountHolderType;
import com.github.bank.util.Enum.AccountType;
import com.github.bank.model.Entity.IndividualAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndividualAccountRepository extends JpaRepository<IndividualAccount, Long> {
    List<IndividualAccount> findByUserIdAndAccountHolderTypeAndAccountType(
            Long accountHolder, AccountHolderType accountHolderType, AccountType accountType);

    List<IndividualAccount> findByUserId(Long userId);
}
