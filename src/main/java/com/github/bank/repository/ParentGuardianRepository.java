package com.github.bank.repository;

import com.github.bank.model.Entity.ParentGuardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParentGuardianRepository extends JpaRepository<ParentGuardian, Long> {
    List<ParentGuardian> findByMinorId(Long minorId);

    List<ParentGuardian> findByGuardianId(Long guardianId);
}