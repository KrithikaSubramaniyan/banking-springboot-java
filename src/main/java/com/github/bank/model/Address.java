package com.github.bank.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Embeddable
public class Address {
    private String houseNumber;
    @NotBlank
    private String street;
    @NotBlank
    private String town;
    @NotBlank
    private String state;
    @NotBlank
    private String postCode;
    @NotBlank
    private String country;
}
