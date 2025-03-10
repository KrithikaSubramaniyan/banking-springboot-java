package com.github.bank.model.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.bank.model.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"guardians", "accounts"})
@Entity
@Table(name = "individualuser")
public class IndividualUser extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column(name = "nationalid")
    private String nationalId;
    @Column(name = "dateofbirth")
    private Date dateOfBirth;
    @Column(name = "phonenumber")
    private String phoneNumber;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "houseNumber", column = @Column(name = "housenumber")),
            @AttributeOverride(name = "postCode", column = @Column(name = "postcode"))
    })
    private Address address;

    @OneToMany(mappedBy = "minor", cascade = CascadeType.ALL)
    @JsonManagedReference("minor-guardian")
    private List<ParentGuardian> guardians;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference("client-account")
    private Set<IndividualAccount> accounts;
}
