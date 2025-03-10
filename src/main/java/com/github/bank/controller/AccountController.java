package com.github.bank.controller;

import com.github.bank.model.DTO.IndividualAccountDTO;
import com.github.bank.service.CorporateAccountService;
import com.github.bank.service.IndividualAccountService;
import com.github.bank.util.Enum.UserType;
import com.github.bank.model.DTO.AuthorizedUserDto;
import com.github.bank.model.DTO.CorporateAccountDTO;
import com.github.bank.model.Entity.JointAccount;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@Slf4j
public class AccountController {

    private final IndividualAccountService individualAccountService;
    private final CorporateAccountService corporateAccountService;

    @Autowired
    public AccountController(IndividualAccountService individualAccountService, CorporateAccountService corporateAccountService) {
        this.individualAccountService = individualAccountService;
        this.corporateAccountService = corporateAccountService;
    }

    @PostMapping("/individual")
    public ResponseEntity<String> openIndividualAccount(@RequestBody @Valid IndividualAccountDTO accountDTO) {
        Long id = individualAccountService.createAccount(accountDTO);
        String message = "Account " + id + " created successfully!";
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping("/corporate")
    public ResponseEntity<String> openCorporateAccount(@RequestBody @Valid CorporateAccountDTO accountDTO) {
        Long id = corporateAccountService.createAccount(accountDTO);
        String message = "Account " + id + " created successfully!";
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping("/add-joint-account/{accountId}")
    public ResponseEntity<JointAccount> addJointAccount(
            @PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
            @RequestParam @NotNull(message = "Primary User ID cannot be null") Long primaryUserId,
            @RequestParam @NotNull(message = "Secondary User ID cannot be null") Long secondaryUserId) {
        JointAccount jointAccount = individualAccountService.addJointAccountUsers(accountId, primaryUserId, secondaryUserId);
        return new ResponseEntity<>(jointAccount, HttpStatus.CREATED);
    }

    // TODO: how are these users used.
    @PostMapping("/{accountId}/add-authorized-user")
    public ResponseEntity<String> addAuthorizedUser(
            @PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
            @RequestBody @Valid AuthorizedUserDto userDto) {
        corporateAccountService.addAuthorizedUser(accountId, userDto);
        String message = "User added successfully for corporate account " + accountId;
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping("/freeze-account/{accountId}")
    public ResponseEntity<String> freezeAccount(
            @PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
            @RequestParam @NotNull(message = "User Type(Corporate/Person) cannot be null") UserType userType) {
        if (UserType.Corporate.equals(userType)) {
            corporateAccountService.freezeAccount(accountId);
        } else {
            individualAccountService.freezeAccount(accountId);
        }
        String message = "Account " + accountId + " frozen successfully";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/close-account/{accountId}")
    public ResponseEntity<String> closeAccount(
            @PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
            @RequestParam @NotNull(message = "User Type(Corporate/Person) cannot be null") UserType userType) {
        if (UserType.Corporate.equals(userType)) {
            corporateAccountService.closeAccount(accountId);
        } else {
            individualAccountService.closeAccount(accountId);
        }
        String message = "Account " + accountId + " closed successfully";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/unfreeze-account/{accountId}")
    public ResponseEntity<String> unfreezeAccount(
            @PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
            @RequestParam @NotNull(message = "User Type(Corporate/Person) cannot be null") UserType userType) {
        if (UserType.Corporate.equals(userType)) {
            corporateAccountService.unFreezeAccount(accountId);
        } else {
            individualAccountService.unFreezeAccount(accountId);
        }
        String message = "Account " + accountId + " unfrozen successfully";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
