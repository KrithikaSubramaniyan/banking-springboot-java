package com.github.bank.controller;

import com.github.bank.service.CorporateAccountService;
import com.github.bank.service.IndividualAccountService;
import com.github.bank.util.Enum.UserType;
import com.github.bank.model.DTO.AuthorizedUserDto;
import com.github.bank.model.DTO.CorporateAccountDTO;
import com.github.bank.model.DTO.IndividualAccountDTO;
import com.github.bank.model.Entity.JointAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private IndividualAccountService individualAccountService;

    @Mock
    private CorporateAccountService corporateAccountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        reset(individualAccountService, corporateAccountService);
    }

    @Test
    void testOpenIndividualAccount() {
        IndividualAccountDTO accountDTO = new IndividualAccountDTO();
        when(individualAccountService.createAccount(accountDTO)).thenReturn(1L);

        ResponseEntity<String> response = accountController.openIndividualAccount(accountDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Account 1 created successfully!", response.getBody());
        verify(individualAccountService, times(1)).createAccount(accountDTO);
    }

    @Test
    void testOpenCorporateAccount() {
        CorporateAccountDTO accountDTO = new CorporateAccountDTO();
        when(corporateAccountService.createAccount(accountDTO)).thenReturn(2L);

        ResponseEntity<String> response = accountController.openCorporateAccount(accountDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Account 2 created successfully!", response.getBody());
        verify(corporateAccountService, times(1)).createAccount(accountDTO);
    }

    @Test
    void testFreezeIndividualAccount() {
        Long accountId = 3L;

        ResponseEntity<String> response = accountController.freezeAccount(accountId, UserType.Person);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account 3 frozen successfully", response.getBody());
        verify(individualAccountService, times(1)).freezeAccount(accountId);
        verifyNoInteractions(corporateAccountService);
    }

    @Test
    void testFreezeCorporateAccount() {
        Long accountId = 4L;

        ResponseEntity<String> response = accountController.freezeAccount(accountId, UserType.Corporate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account 4 frozen successfully", response.getBody());
        verify(corporateAccountService, times(1)).freezeAccount(accountId);
        verifyNoInteractions(individualAccountService);
    }

    @Test
    void testCloseIndividualAccount() {
        Long accountId = 5L;

        ResponseEntity<String> response = accountController.closeAccount(accountId, UserType.Person);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account 5 closed successfully", response.getBody());
        verify(individualAccountService, times(1)).closeAccount(accountId);
        verifyNoInteractions(corporateAccountService);
    }

    @Test
    void testCloseCorporateAccount() {
        Long accountId = 6L;

        ResponseEntity<String> response = accountController.closeAccount(accountId, UserType.Corporate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account 6 closed successfully", response.getBody());
        verify(corporateAccountService, times(1)).closeAccount(accountId);
        verifyNoInteractions(individualAccountService);
    }

    @Test
    void testUnfreezeIndividualAccount() {
        Long accountId = 7L;

        ResponseEntity<String> response = accountController.unfreezeAccount(accountId, UserType.Person);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account 7 unfrozen successfully", response.getBody());
        verify(individualAccountService, times(1)).unFreezeAccount(accountId);
        verifyNoInteractions(corporateAccountService);
    }

    @Test
    void testUnfreezeCorporateAccount() {
        Long accountId = 8L;

        ResponseEntity<String> response = accountController.unfreezeAccount(accountId, UserType.Corporate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account 8 unfrozen successfully", response.getBody());
        verify(corporateAccountService, times(1)).unFreezeAccount(accountId);
        verifyNoInteractions(individualAccountService);
    }

    @Test
    void testAddAuthorizedUser() {
        Long accountId = 9L;
        AuthorizedUserDto userDto = new AuthorizedUserDto();

        ResponseEntity<String> response = accountController.addAuthorizedUser(accountId, userDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User added successfully for corporate account 9", response.getBody());
        verify(corporateAccountService, times(1)).addAuthorizedUser(accountId, userDto);
    }

    @Test
    void testAddJointAccount() {
        Long accountId = 10L;
        Long primaryUserId = 1L;
        Long secondaryUserId = 2L;
        JointAccount jointAccount = new JointAccount();

        when(individualAccountService.addJointAccountUsers(accountId, primaryUserId, secondaryUserId))
                .thenReturn(jointAccount);

        ResponseEntity<JointAccount> response = accountController.addJointAccount(accountId, primaryUserId, secondaryUserId);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(jointAccount, response.getBody());
        verify(individualAccountService, times(1)).addJointAccountUsers(
                accountId, primaryUserId, secondaryUserId);
    }
}
