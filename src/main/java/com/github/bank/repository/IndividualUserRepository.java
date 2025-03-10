package com.github.bank.repository;

import com.github.bank.model.Entity.IndividualUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualUserRepository extends JpaRepository<IndividualUser, Long> {
}
