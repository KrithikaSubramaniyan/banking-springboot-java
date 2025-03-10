package com.github.bank.repository;

import com.github.bank.model.Entity.AuthorizedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedUserRepository extends JpaRepository<AuthorizedUser, Long> {
}
