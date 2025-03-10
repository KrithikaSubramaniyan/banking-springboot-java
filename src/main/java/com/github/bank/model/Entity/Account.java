package com.github.bank.model.Entity;

import com.github.bank.util.Enum.UserType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "usertype", discriminatorType = DiscriminatorType.STRING)
public abstract class Account extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "usertype", insertable = false, updatable = false)
    private UserType userType;
}