package com.github.bank.model.Entity;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "jointaccount")
public class JointAccount extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "accountid", nullable = false)
    private IndividualAccount accountId;
    @ManyToOne
    @JoinColumn(name = "primaryuserid", nullable = false)
    private IndividualUser primaryUserId;
    @ManyToOne
    @JoinColumn(name = "secondaryuserid", nullable = false)
    private IndividualUser secondaryUserId;
}