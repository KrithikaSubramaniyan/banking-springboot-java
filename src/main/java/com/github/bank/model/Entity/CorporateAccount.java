package com.github.bank.model.Entity;

import com.github.bank.model.Address;
import com.github.bank.util.Enum.Currency;
import com.github.bank.util.Enum.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "corporateaccount")
@DiscriminatorValue("Corporate")
public class CorporateAccount extends Account {

    @Column(name = "companyname", nullable = false)
    private String companyName;

    @Column(name = "registrationnumber", nullable = false)
    private String registrationNumber;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "houseNumber", column = @Column(name = "housenumber")),
            @AttributeOverride(name = "postCode", column = @Column(name = "postcode"))
    })
    private Address address;

    @Column(name = "accountstatus")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(name = "accountbalance")
    private BigDecimal accountBalance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @OneToMany(mappedBy = "corporateAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthorizedUser> authorizedUsers = new ArrayList<>();
}
