package com.github.bank.repository;

import com.github.bank.model.Entity.CorporateAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorporateAccountRepository extends JpaRepository<CorporateAccount, Long> {
}
