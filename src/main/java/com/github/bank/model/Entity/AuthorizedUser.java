package com.github.bank.model.Entity;

import com.github.bank.util.Enum.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "corporateuser")
public class AuthorizedUser extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "corporateaccountid", nullable = false)
    private CorporateAccount corporateAccount;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
}
