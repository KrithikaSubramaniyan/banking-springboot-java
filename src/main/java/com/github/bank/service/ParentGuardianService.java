package com.github.bank.service;

import com.github.bank.model.Entity.IndividualUser;
import com.github.bank.model.Entity.ParentGuardian;
import com.github.bank.util.Enum.RelationshipType;
import com.github.bank.repository.IndividualAccountRepository;
import com.github.bank.repository.IndividualUserRepository;
import com.github.bank.repository.ParentGuardianRepository;
import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.ParentGuardianDTO;
import com.github.bank.model.Entity.IndividualAccount;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParentGuardianService {

    private final ParentGuardianRepository parentGuardianRepository;
    private final IndividualUserRepository individualUserRepository;
    private final IndividualAccountRepository individualAccountRepository;

    @Autowired
    public ParentGuardianService(ParentGuardianRepository parentGuardianRepository,
                                 IndividualUserRepository individualUserRepository,
                                 IndividualAccountRepository individualAccountRepository) {
        this.parentGuardianRepository = parentGuardianRepository;
        this.individualUserRepository = individualUserRepository;
        this.individualAccountRepository = individualAccountRepository;
    }

    @Transactional
    public void linkGuardianToMinor(Long minorId, Long guardianId, RelationshipType relationshipType) {
        IndividualUser minor = individualUserRepository.findById(minorId)
                .orElseThrow(() -> new ResourceNotFoundException("Minor not found with user ID " + minorId));

        IndividualUser guardian = individualUserRepository.findById(guardianId)
                .orElseThrow(() -> new ResourceNotFoundException("Guardian not found with user ID " + guardianId));

        List<IndividualAccount> guardianAccount = individualAccountRepository.findByUserId(guardianId);
        if (guardianAccount.size() == 0) {
            throw new ResourceNotFoundException("Guardian/parent doesn't have a personal account.");
        }
        ParentGuardian parentGuardian = new ParentGuardian();
        parentGuardian.setMinor(minor);
        parentGuardian.setGuardian(guardian);
        parentGuardian.setRelationshipType(relationshipType);

        parentGuardianRepository.save(parentGuardian);
    }

    public List<ParentGuardianDTO> getGuardiansForMinor(Long minorId) {
        List<ParentGuardian> parentGuardians = parentGuardianRepository.findByMinorId(minorId);
        List<ParentGuardianDTO> parentGuardianDTOs = new ArrayList<>();
        parentGuardians.stream().forEach(parentGuardian -> {
            ParentGuardianDTO parentGuardianDTO = new ParentGuardianDTO();
            parentGuardianDTO.setMinor(parentGuardian.getMinor());
            parentGuardianDTO.setGuardian(parentGuardian.getGuardian());
            parentGuardianDTOs.add(parentGuardianDTO);
        });
        return parentGuardianDTOs;
    }

    public List<ParentGuardianDTO> getMinorsForGuardian(Long guardianId) {
        List<ParentGuardian> parentGuardians = parentGuardianRepository.findByGuardianId(guardianId);
        List<ParentGuardianDTO> parentGuardianDTOs = new ArrayList<>();
        parentGuardians.stream().forEach(parentGuardian -> {
            ParentGuardianDTO parentGuardianDTO = new ParentGuardianDTO();
            parentGuardianDTO.setMinor(parentGuardian.getMinor());
            parentGuardianDTO.setGuardian(parentGuardian.getGuardian());
            parentGuardianDTOs.add(parentGuardianDTO);
        });
        return parentGuardianDTOs;
    }
}
