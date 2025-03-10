package com.github.bank.service;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.IndividualAccountDTO;
import com.github.bank.model.Entity.IndividualAccount;
import com.github.bank.model.Entity.IndividualUser;
import com.github.bank.model.Entity.JointAccount;
import com.github.bank.repository.AccountRepository;
import com.github.bank.repository.IndividualAccountRepository;
import com.github.bank.repository.IndividualUserRepository;
import com.github.bank.repository.JointAccountRepository;
import com.github.bank.util.Enum.AccountHolderType;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.AccountType;
import com.github.bank.util.Enum.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IndividualAccountServiceTest {

    @Mock
    private IndividualAccountRepository individualAccountRepository;

    @Mock
    private IndividualUserRepository individualUserRepository;

    @Mock
    private JointAccountRepository jointAccountRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private IndividualAccountService individualAccountService;

    private IndividualAccountDTO accountDTO;
    private IndividualUser user;
    private IndividualAccount individualAccount;
    private JointAccount jointAccount;

    @BeforeEach
    public void setUp() throws ParseException {
        accountDTO = new IndividualAccountDTO();
        accountDTO.setAccountholder(1L);
        accountDTO.setAccountHolderType(AccountHolderType.Personal);
        accountDTO.setAccountType(AccountType.Current);
        accountDTO.setCurrency(Currency.USD);
        accountDTO.setAccountBalance(BigDecimal.valueOf(1000));

        user = new IndividualUser();
        user.setId(1L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = sdf.parse("2000-01-01");
        user.setDateOfBirth(dob);
        user.setGuardians(new ArrayList<>());

        individualAccount = new IndividualAccount();
        individualAccount.setId(1L);
        individualAccount.setAccountStatus(AccountStatus.Active);
        individualAccount.setAccountType(AccountType.Current);
        individualAccount.setAccountHolderType(AccountHolderType.Personal);
        individualAccount.setAccountBalance(BigDecimal.valueOf(1000));
        individualAccount.setUser(user);

        jointAccount = new JointAccount();
        jointAccount.setId(1L);
    }

    @Test
    public void testCreateAccount() {
        when(individualUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(individualAccountRepository.save(any(IndividualAccount.class))).thenReturn(individualAccount);

        Long accountId = individualAccountService.createAccount(accountDTO);

        assertNotNull(accountId);
        assertEquals(1L, accountId);
        verify(individualAccountRepository, times(1)).save(any(IndividualAccount.class));
    }

    @Test
    public void testCreateAccount_UserNotFound() {
        when(individualUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            individualAccountService.createAccount(accountDTO);
        });
    }

    @Test
    public void testCreateAccount_AlreadyHasPersonalCurrentAccount() {
        when(individualUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(individualAccountRepository.findByUserIdAndAccountHolderTypeAndAccountType(1L, AccountHolderType.Personal, AccountType.Current))
                .thenReturn(List.of(individualAccount));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            individualAccountService.createAccount(accountDTO);
        });
        assertEquals("The user already have a personal current account.", exception.getMessage());
    }

    @Test
    public void testAddJointAccountUsers() {
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.of(individualAccount));
        when(individualUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(individualUserRepository.findById(2L)).thenReturn(Optional.of(new IndividualUser()));
        when(jointAccountRepository.countJointAccounts(1L, 2L)).thenReturn(0L);
        when(jointAccountRepository.save(any(JointAccount.class))).thenReturn(jointAccount);

        JointAccount createdJointAccount = individualAccountService.addJointAccountUsers(1L, 1L, 2L);

        assertNotNull(createdJointAccount);
        assertEquals(1L, createdJointAccount.getId());
        verify(jointAccountRepository, times(1)).save(any(JointAccount.class));
    }

    @Test
    public void testAddJointAccountUsers_AccountNotFound() {
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            individualAccountService.addJointAccountUsers(1L, 1L, 2L);
        });
    }

    @Test
    public void testAddJointAccountUsers_UserNotFound() {
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.of(individualAccount));
        when(individualUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            individualAccountService.addJointAccountUsers(1L, 1L, 2L);
        });
    }

    @Test
    public void testFreezeAccount() {
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.of(individualAccount));
        when(individualAccountRepository.save(any(IndividualAccount.class))).thenReturn(individualAccount);

        IndividualAccount frozenAccount = individualAccountService.freezeAccount(1L);

        assertNotNull(frozenAccount);
        Assertions.assertEquals(AccountStatus.Frozen, frozenAccount.getAccountStatus());
        verify(individualAccountRepository, times(1)).save(individualAccount);
    }

    @Test
    public void testFreezeAccount_NotFound() {
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            individualAccountService.freezeAccount(1L);
        });
    }

    @Test
    public void testUnFreezeAccount() {
        individualAccount.setAccountStatus(AccountStatus.Frozen);
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.of(individualAccount));
        when(individualAccountRepository.save(any(IndividualAccount.class))).thenReturn(individualAccount);

        IndividualAccount activeAccount = individualAccountService.unFreezeAccount(1L);

        assertNotNull(activeAccount);
        Assertions.assertEquals(AccountStatus.Active, activeAccount.getAccountStatus());
        verify(individualAccountRepository, times(1)).save(individualAccount);
    }

    @Test
    public void testUnFreezeAccount_NotFound() {
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            individualAccountService.unFreezeAccount(1L);
        });
    }

    @Test
    public void testCloseAccount() {
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.of(individualAccount));
        when(individualAccountRepository.save(any(IndividualAccount.class))).thenReturn(individualAccount);

        IndividualAccount closedAccount = individualAccountService.closeAccount(1L);

        assertNotNull(closedAccount);
        Assertions.assertEquals(AccountStatus.Closed, closedAccount.getAccountStatus());
        verify(individualAccountRepository, times(1)).save(individualAccount);
    }

    @Test
    public void testCloseAccount_NotFound() {
        when(individualAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            individualAccountService.closeAccount(1L);
        });
    }
}
