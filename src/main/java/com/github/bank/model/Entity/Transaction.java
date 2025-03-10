package com.github.bank.model.Entity;

import com.github.bank.util.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "accountid")
    private Account accountId;
    @Column(name = "transactiontype")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Column(name = "transactionamount")
    private BigDecimal transactionAmount;

    //Audit columns
    @CreatedDate
    @Column(name = "createdat", updatable = false)
    private LocalDateTime createdAt;
    @CreatedBy
    @Column(name = "createdby", updatable = false)
    private String createdBy;
}
