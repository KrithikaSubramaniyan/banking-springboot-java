package com.github.bank.model.DTO;

import com.github.bank.util.Enum.AccountHolderType;
import com.github.bank.util.Enum.AccountType;
import com.github.bank.util.Enum.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IndividualAccountDTO {
    @NotNull
    private Long accountholder;
    @NotNull
    private AccountHolderType accountHolderType;
    @NotNull
    private AccountType accountType;
    @NotNull
    private BigDecimal accountBalance;
    private Currency currency = Currency.EUR;
}
