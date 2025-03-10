package com.github.bank.model.DTO;

import com.github.bank.model.Address;
import com.github.bank.util.Enum.Currency;
import com.github.bank.util.Enum.Role;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CorporateAccountDTO {
    @NotBlank
    private String companyName;
    @NotBlank
    private String registrationNumber;
    @NotNull
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    @Embedded
    private Address address;
    @NotNull
    private BigDecimal accountBalance;
    private Currency currency = Currency.EUR;
    @NotBlank
    private String fullName;
    @NotBlank
    @Email
    private String email;
    @NotNull
    private Role role;
}