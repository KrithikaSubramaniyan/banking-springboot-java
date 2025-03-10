package com.github.bank.service;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.AuthorizedUserDto;
import com.github.bank.model.DTO.CorporateAccountDTO;
import com.github.bank.model.Entity.AuthorizedUser;
import com.github.bank.model.Entity.CorporateAccount;
import com.github.bank.repository.AuthorizedUserRepository;
import com.github.bank.repository.CorporateAccountRepository;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.UserType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorporateAccountService {

    private final CorporateAccountRepository corporateAccountRepository;
    private final AuthorizedUserRepository authorizedUserRepository;

    @Autowired
    public CorporateAccountService(CorporateAccountRepository corporateAccountRepository, AuthorizedUserRepository authorizedUserRepository) {
        this.corporateAccountRepository = corporateAccountRepository;
        this.authorizedUserRepository = authorizedUserRepository;
    }

    @Transactional
    public Long createAccount(CorporateAccountDTO accountDTO) {
        CorporateAccount corporateAccount = new CorporateAccount();
        corporateAccount.setUserType(UserType.Corporate);
        corporateAccount.setAccountStatus(AccountStatus.Active);
        corporateAccount.setAccountBalance(accountDTO.getAccountBalance());
        corporateAccount.setCompanyName(accountDTO.getCompanyName());
        corporateAccount.setRegistrationNumber(accountDTO.getRegistrationNumber());
        corporateAccount.setAddress(accountDTO.getAddress());
        corporateAccount.setPhoneNumber(accountDTO.getPhoneNumber());
        corporateAccount.setCurrency(accountDTO.getCurrency());
        CorporateAccount newCorporateAccount = corporateAccountRepository.save(corporateAccount);
        AuthorizedUser authorizedUser = new AuthorizedUser();
        authorizedUser.setCorporateAccount(newCorporateAccount);
        authorizedUser.setRole(accountDTO.getRole());
        authorizedUser.setEmail(accountDTO.getEmail());
        authorizedUser.setFullName(accountDTO.getFullName());
        authorizedUserRepository.save(authorizedUser);
        return newCorporateAccount.getId();
    }

    @Transactional
    public CorporateAccount freezeAccount(Long accountId) {
        CorporateAccount account = corporateAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID " + accountId));
        if (!AccountStatus.Frozen.equals(account.getAccountStatus())) {
            account.setAccountStatus(AccountStatus.Frozen);
            corporateAccountRepository.save(account);
            return account;
        }
        return null;
    }

    @Transactional
    public CorporateAccount unFreezeAccount(Long accountId) {
        CorporateAccount account = corporateAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID " + accountId));
        if (!AccountStatus.Active.equals(account.getAccountStatus())) {
            account.setAccountStatus(AccountStatus.Active);
            corporateAccountRepository.save(account);
            return account;
        }
        return null;
    }

    @Transactional
    public CorporateAccount closeAccount(Long accountId) {
        CorporateAccount account = corporateAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID " + accountId));
        if (!AccountStatus.Closed.equals(account.getAccountStatus())) {
            account.setAccountStatus(AccountStatus.Closed);
            corporateAccountRepository.save(account);
            return account;
        }
        return null;
    }

    @Transactional
    public void addAuthorizedUser(Long accountId, AuthorizedUserDto userDto) {
        CorporateAccount corporateAccount = corporateAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Corporate Account not found with ID " + accountId));
        AuthorizedUser user = new AuthorizedUser();
        user.setCorporateAccount(corporateAccount);
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());

        authorizedUserRepository.save(user);
    }
}