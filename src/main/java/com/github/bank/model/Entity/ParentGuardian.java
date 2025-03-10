package com.github.bank.model.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.bank.util.Enum.RelationshipType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parentguardian")
@Getter
@Setter
@ToString(exclude = "minor")
public class ParentGuardian extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "minorid", nullable = false)
    @JsonBackReference("minor-guardian")
    private IndividualUser minor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guardianid", nullable = false)
    @JsonIgnore
    private IndividualUser guardian;

    @Column(name = "relationshiptype", nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationshipType relationshipType;
}
