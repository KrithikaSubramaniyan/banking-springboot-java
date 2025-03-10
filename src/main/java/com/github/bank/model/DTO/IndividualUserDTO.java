package com.github.bank.model.DTO;

import com.github.bank.model.Address;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

@Data
public class IndividualUserDTO {
    // TODO: are these fields ok ? can we make anyfield like email/nationalId as unique. if we need to update passing Client ID might not appropiate
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String nationalId;
    @NotNull
    private Date dateOfBirth;
    @NotNull
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    @Embedded
    private Address address;
}
