package com.github.bank.service;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.ParentGuardianDTO;
import com.github.bank.model.Entity.IndividualAccount;
import com.github.bank.model.Entity.IndividualUser;
import com.github.bank.model.Entity.ParentGuardian;
import com.github.bank.repository.IndividualAccountRepository;
import com.github.bank.repository.IndividualUserRepository;
import com.github.bank.repository.ParentGuardianRepository;
import com.github.bank.util.Enum.RelationshipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParentGuardianServiceTest {

    @Mock
    private ParentGuardianRepository parentGuardianRepository;

    @Mock
    private IndividualUserRepository individualUserRepository;

    @Mock
    private IndividualAccountRepository individualAccountRepository;

    @InjectMocks
    private ParentGuardianService parentGuardianService;

    private IndividualUser minor;
    private IndividualUser guardian;
    private IndividualAccount guardianAccount;
    private ParentGuardian parentGuardian;

    @BeforeEach
    public void setUp() {
        minor = new IndividualUser();
        minor.setId(1L);

        guardian = new IndividualUser();
        guardian.setId(2L);

        guardianAccount = new IndividualAccount();
        guardianAccount.setId(1L);
        guardianAccount.setUser(guardian);

        parentGuardian = new ParentGuardian();
        parentGuardian.setMinor(minor);
        parentGuardian.setGuardian(guardian);
        parentGuardian.setRelationshipType(RelationshipType.Parent);
    }

    @Test
    public void testLinkGuardianToMinor_Success() {
        when(individualUserRepository.findById(1L)).thenReturn(java.util.Optional.of(minor));
        when(individualUserRepository.findById(2L)).thenReturn(java.util.Optional.of(guardian));

        when(individualAccountRepository.findByUserId(2L)).thenReturn(Arrays.asList(guardianAccount));

        parentGuardianService.linkGuardianToMinor(1L, 2L, RelationshipType.Parent);

        verify(parentGuardianRepository, times(1)).save(parentGuardian);
    }

    @Test
    public void testLinkGuardianToMinor_GuardianNotFound() {
        when(individualUserRepository.findById(1L)).thenReturn(java.util.Optional.of(minor));
        when(individualUserRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            parentGuardianService.linkGuardianToMinor(1L, 2L, RelationshipType.Parent);
        });

        assertEquals("Guardian not found with user ID 2", exception.getMessage());
    }

    @Test
    public void testLinkGuardianToMinor_GuardianWithoutAccount() {
        when(individualUserRepository.findById(1L)).thenReturn(java.util.Optional.of(minor));
        when(individualUserRepository.findById(2L)).thenReturn(java.util.Optional.of(guardian));

        when(individualAccountRepository.findByUserId(2L)).thenReturn(Arrays.asList());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            parentGuardianService.linkGuardianToMinor(1L, 2L, RelationshipType.Parent);
        });

        assertEquals("Guardian/parent doesn't have a personal account.", exception.getMessage());
    }

    @Test
    public void testGetGuardiansForMinor() {
        ParentGuardian parentGuardian = new ParentGuardian();
        parentGuardian.setMinor(minor);
        parentGuardian.setGuardian(guardian);

        when(parentGuardianRepository.findByMinorId(1L)).thenReturn(Arrays.asList(parentGuardian));

        List<ParentGuardianDTO> guardians = parentGuardianService.getGuardiansForMinor(1L);

        assertEquals(1, guardians.size());
        assertEquals(2L, guardians.get(0).getGuardian().getId());
    }

    @Test
    public void testGetMinorsForGuardian() {
        ParentGuardian parentGuardian = new ParentGuardian();
        parentGuardian.setMinor(minor);
        parentGuardian.setGuardian(guardian);

        when(parentGuardianRepository.findByGuardianId(2L)).thenReturn(Arrays.asList(parentGuardian));

        List<ParentGuardianDTO> minors = parentGuardianService.getMinorsForGuardian(2L);

        assertEquals(1, minors.size());
        assertEquals(1L, minors.get(0).getMinor().getId());
    }
}
