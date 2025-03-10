package com.github.bank.model.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.bank.util.Enum.AccountHolderType;
import com.github.bank.util.Enum.AccountStatus;
import com.github.bank.util.Enum.AccountType;
import com.github.bank.util.Enum.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "user")
@Entity
@Table(name = "individualaccount")
@DiscriminatorValue("Person")
public class IndividualAccount extends Account {
    @Column(name = "accountholdertype")
    @Enumerated(EnumType.STRING)
    private AccountHolderType accountHolderType;

    @Column(name = "accounttype")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "accountstatus")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(name = "accountbalance")
    private BigDecimal accountBalance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "accountholder", nullable = false)
    @JsonBackReference("client-account")
    private IndividualUser user;

    @ManyToMany
    @JoinTable(name = "jointaccount",
            joinColumns = @JoinColumn(name = "accountid"),
            inverseJoinColumns = @JoinColumn(name = "secondaryuserid"))
    private List<IndividualUser> jointAccountHolders;
}
