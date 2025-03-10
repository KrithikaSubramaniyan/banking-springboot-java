package com.github.bank.service;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.Address;
import com.github.bank.model.DTO.AuthorizedUserDto;
import com.github.bank.model.DTO.CorporateAccountDTO;
import com.github.bank.model.Entity.AuthorizedUser;
import com.github.bank.model.Entity.CorporateAccount;
import com.github.bank.repository.AuthorizedUserRepository;
import com.github.bank.repository.CorporateAccountRepository;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.Currency;
import com.github.bank.util.Enum.Role;
import com.github.bank.util.Enum.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CorporateAccountServiceTest {

    @Mock
    private CorporateAccountRepository corporateAccountRepository;

    @Mock
    private AuthorizedUserRepository authorizedUserRepository;

    @InjectMocks
    private CorporateAccountService corporateAccountService;

    private CorporateAccountDTO accountDTO;
    private AuthorizedUserDto authorizedUserDto;
    private CorporateAccount corporateAccount;

    @BeforeEach
    public void setUp() {
        accountDTO = new CorporateAccountDTO();
        accountDTO.setAccountBalance(BigDecimal.valueOf(10000));
        accountDTO.setCompanyName("Test Corp");
        accountDTO.setRegistrationNumber("123456");
        Address address = new Address();
        address.setHouseNumber("123");
        address.setStreet("Main St");
        address.setTown("Springfield");
        address.setState("IL");
        address.setCountry("US");
        address.setPostCode("62701");
        accountDTO.setAddress(address);
        accountDTO.setPhoneNumber("123-456-7890");
        accountDTO.setCurrency(Currency.USD);
        accountDTO.setRole(Role.ADMIN);
        accountDTO.setEmail("admin@testcorp.com");
        accountDTO.setFullName("Test Admin");

        authorizedUserDto = new AuthorizedUserDto();
        authorizedUserDto.setFullName("Test Admin");
        authorizedUserDto.setEmail("admin@testcorp.com");
        authorizedUserDto.setRole(Role.ADMIN);

        corporateAccount = new CorporateAccount();
        corporateAccount.setId(1L);
        corporateAccount.setUserType(UserType.Corporate);
        corporateAccount.setAccountStatus(AccountStatus.Active);
        corporateAccount.setAccountBalance(BigDecimal.valueOf(10000));
        corporateAccount.setCompanyName("Test Corp");
        corporateAccount.setRegistrationNumber("123456");
        corporateAccount.setAddress(address);
        corporateAccount.setPhoneNumber("123-456-7890");
        corporateAccount.setCurrency(Currency.USD);
    }

    @Test
    public void testCreateAccount() {
        when(corporateAccountRepository.save(any(CorporateAccount.class))).thenReturn(corporateAccount);
        when(authorizedUserRepository.save(any(AuthorizedUser.class))).thenReturn(new AuthorizedUser());

        Long accountId = corporateAccountService.createAccount(accountDTO);

        assertNotNull(accountId);
        assertEquals(1L, accountId);
        verify(corporateAccountRepository, times(1)).save(any(CorporateAccount.class));
        verify(authorizedUserRepository, times(1)).save(any(AuthorizedUser.class));
    }

    @Test
    public void testFreezeAccount() {
        when(corporateAccountRepository.findById(1L)).thenReturn(Optional.of(corporateAccount));
        when(corporateAccountRepository.save(any(CorporateAccount.class))).thenReturn(corporateAccount);

        CorporateAccount frozenAccount = corporateAccountService.freezeAccount(1L);

        assertNotNull(frozenAccount);
        Assertions.assertEquals(AccountStatus.Frozen, frozenAccount.getAccountStatus());
        verify(corporateAccountRepository, times(1)).save(corporateAccount);
    }

    @Test
    public void testFreezeAccount_NotFound() {
        when(corporateAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            corporateAccountService.freezeAccount(1L);
        });
    }

    @Test
    public void testUnFreezeAccount() {
        corporateAccount.setAccountStatus(AccountStatus.Frozen);
        when(corporateAccountRepository.findById(1L)).thenReturn(Optional.of(corporateAccount));
        when(corporateAccountRepository.save(any(CorporateAccount.class))).thenReturn(corporateAccount);

        CorporateAccount activeAccount = corporateAccountService.unFreezeAccount(1L);

        assertNotNull(activeAccount);
        Assertions.assertEquals(AccountStatus.Active, activeAccount.getAccountStatus());
        verify(corporateAccountRepository, times(1)).save(corporateAccount);
    }

    @Test
    public void testUnFreezeAccount_NotFound() {
        when(corporateAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            corporateAccountService.unFreezeAccount(1L);
        });
    }

    @Test
    public void testCloseAccount() {
        corporateAccount.setAccountStatus(AccountStatus.Active);
        when(corporateAccountRepository.findById(1L)).thenReturn(Optional.of(corporateAccount));
        when(corporateAccountRepository.save(any(CorporateAccount.class))).thenReturn(corporateAccount);

        CorporateAccount closedAccount = corporateAccountService.closeAccount(1L);

        assertNotNull(closedAccount);
        Assertions.assertEquals(AccountStatus.Closed, closedAccount.getAccountStatus());
        verify(corporateAccountRepository, times(1)).save(corporateAccount);
    }

    @Test
    public void testCloseAccount_NotFound() {
        when(corporateAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            corporateAccountService.closeAccount(1L);
        });
    }

    @Test
    public void testAddAuthorizedUser() {
        when(corporateAccountRepository.findById(1L)).thenReturn(Optional.of(corporateAccount));

        corporateAccountService.addAuthorizedUser(1L, authorizedUserDto);

        verify(authorizedUserRepository, times(1)).save(any(AuthorizedUser.class));
    }

    @Test
    public void testAddAuthorizedUser_NotFound() {
        when(corporateAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            corporateAccountService.addAuthorizedUser(1L, authorizedUserDto);
        });
    }
}
