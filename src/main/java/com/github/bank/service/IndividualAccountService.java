package com.github.bank.service;

import com.github.bank.model.DTO.IndividualAccountDTO;
import com.github.bank.model.Entity.IndividualUser;
import com.github.bank.model.Entity.ParentGuardian;
import com.github.bank.repository.IndividualUserRepository;
import com.github.bank.util.AgeValidator;
import com.github.bank.repository.IndividualAccountRepository;
import com.github.bank.repository.JointAccountRepository;
import com.github.bank.util.Enum.AccountHolderType;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.AccountType;
import com.github.bank.util.Enum.UserType;
import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.Entity.IndividualAccount;
import com.github.bank.model.Entity.JointAccount;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IndividualAccountService {

    private final IndividualAccountRepository individualAccountRepository;
    private final IndividualUserRepository individualUserRepository;
    private final JointAccountRepository jointAccountRepository;

    @Autowired
    public IndividualAccountService(IndividualAccountRepository individualAccountRepository,
                                    IndividualUserRepository individualUserRepository,
                                    JointAccountRepository jointAccountRepository) {
        this.individualAccountRepository = individualAccountRepository;
        this.individualUserRepository = individualUserRepository;
        this.jointAccountRepository = jointAccountRepository;
    }

    @Transactional

    public Long createAccount(IndividualAccountDTO accountDTO) {
        Long userId = accountDTO.getAccountholder();
        IndividualUser user = individualUserRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (AccountHolderType.Personal.equals(accountDTO.getAccountHolderType())
                && AccountType.Current.equals(accountDTO.getAccountType())) {
            List<IndividualAccount> accountList = individualAccountRepository.findByUserIdAndAccountHolderTypeAndAccountType(
                    user.getId(), accountDTO.getAccountHolderType(), accountDTO.getAccountType());
            if (!accountList.isEmpty()) {
                throw new RuntimeException("The user already have a personal current account.");
            }
        }

        IndividualAccount individualAccount = new IndividualAccount();
        if (AgeValidator.isMinor(user.getDateOfBirth())) {
            List<ParentGuardian> guardians = user.getGuardians();
            if (guardians.isEmpty()) {
                throw new RuntimeException("A minor must have a parent or guardian linked before creating an account.");
            }
            individualAccount.setAccountType(AccountType.Junior);
        } else {
            individualAccount.setAccountType(accountDTO.getAccountType());
        }

        individualAccount.setUserType(UserType.Person);
        individualAccount.setUser(user);
        individualAccount.setAccountStatus(AccountStatus.Active);
        individualAccount.setAccountHolderType(accountDTO.getAccountHolderType());
        individualAccount.setCurrency(accountDTO.getCurrency());
        individualAccount.setAccountBalance(accountDTO.getAccountBalance());
        IndividualAccount newAccount = individualAccountRepository.save(individualAccount);
        log.info("Account created " + newAccount.getId() + " " + newAccount);
        return newAccount.getId();
    }

    @Transactional
    public JointAccount addJointAccountUsers(Long accountId, Long primaryUserId, Long secondaryUserId)
            throws RuntimeException {
        if (jointAccountRepository.countJointAccounts(primaryUserId, secondaryUserId) > 0) {
            throw new RuntimeException("These two individuals already have a joint account.");
        }
        IndividualAccount account = individualAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
        if (!AccountHolderType.Joint.equals(account.getAccountHolderType())) {
            account.setAccountHolderType(AccountHolderType.Joint);
            individualAccountRepository.save(account);
        }
        IndividualUser primaryUser = individualUserRepository.findById(primaryUserId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with ID: " + primaryUserId));

        IndividualUser secondaryUser = individualUserRepository.findById(secondaryUserId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with ID: " + secondaryUserId));
        JointAccount jointAccount = new JointAccount();
        jointAccount.setAccountId(account);
        jointAccount.setPrimaryUserId(primaryUser);
        jointAccount.setSecondaryUserId(secondaryUser);
        return jointAccountRepository.save(jointAccount);
    }

    @Transactional
    public IndividualAccount freezeAccount(Long accountId) {
        IndividualAccount account = individualAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID " + accountId));
        if (!AccountStatus.Frozen.equals(account.getAccountStatus())) {
            account.setAccountStatus(AccountStatus.Frozen);
            individualAccountRepository.save(account);
            return account;
        }
        return null;
    }

    @Transactional
    public IndividualAccount unFreezeAccount(Long accountId) {
        IndividualAccount account = individualAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID " + accountId));
        if (!AccountStatus.Active.equals(account.getAccountStatus())) {
            account.setAccountStatus(AccountStatus.Active);
            individualAccountRepository.save(account);
            return account;
        }
        return null;
    }

    @Transactional
    public IndividualAccount closeAccount(Long accountId) {
        IndividualAccount account = individualAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID " + accountId));
        if (!AccountStatus.Closed.equals(account.getAccountStatus())) {
            account.setAccountStatus(AccountStatus.Closed);
            individualAccountRepository.save(account);
            return account;
        }
        return null;
    }
}
