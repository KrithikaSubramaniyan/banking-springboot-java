package com.github.bank.repository;

import com.github.bank.model.Entity.JointAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JointAccountRepository extends JpaRepository<JointAccount, Long> {
    @Query(value = "SELECT count(j.id) FROM JOINTACCOUNT j " +
            "WHERE (j.primaryuserid = :primaryUserId AND j.secondaryuserid = :secondaryUserId) " +
            "OR (j.primaryuserid = :secondaryUserId AND j.secondaryuserid = :primaryUserId)", nativeQuery = true)
    long countJointAccounts(Long primaryUserId, Long secondaryUserId);

    @Query(value = "SELECT j.id FROM JOINTACCOUNT j " +
            "WHERE (j.secondaryuserid = :userId)", nativeQuery = true)
    List<Long> getJointAccounts(Long userId);

}