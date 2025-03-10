package com.github.bank.controller;

import com.github.bank.service.ParentGuardianService;
import com.github.bank.util.Enum.RelationshipType;
import com.github.bank.model.DTO.ParentGuardianDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentGuardianControllerTest {

    @Mock
    private ParentGuardianService parentGuardianService;

    @InjectMocks
    private ParentGuardianController parentGuardianController;

    @BeforeEach
    void setUp() {
        reset(parentGuardianService);
    }

    @Test
    void testLinkGuardianToMinor() {
        Long minorId = 1L;
        Long guardianId = 2L;
        RelationshipType relationshipType = RelationshipType.Parent;

        doNothing().when(parentGuardianService).linkGuardianToMinor(minorId, guardianId, relationshipType);

        ResponseEntity<String> response = parentGuardianController.linkGuardianToMinor(minorId, guardianId, relationshipType);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Guardian linked successfully.", response.getBody());
        verify(parentGuardianService, times(1)).linkGuardianToMinor(minorId, guardianId, relationshipType);
    }

    @Test
    void testGetGuardiansForMinor() {
        Long minorId = 1L;
        ParentGuardianDTO dto1 = mock(ParentGuardianDTO.class);
        ParentGuardianDTO dto2 = mock(ParentGuardianDTO.class);
        List<ParentGuardianDTO> mockResponse = Arrays.asList(dto1, dto2);

        when(parentGuardianService.getGuardiansForMinor(minorId)).thenReturn(mockResponse);

        ResponseEntity<List<ParentGuardianDTO>> response = parentGuardianController.getGuardiansForMinor(minorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(parentGuardianService, times(1)).getGuardiansForMinor(minorId);
    }

    @Test
    void testGetMinorsForGuardian() {
        Long guardianId = 2L;
        ParentGuardianDTO dto = mock(ParentGuardianDTO.class);
        List<ParentGuardianDTO> mockResponse = Arrays.asList(dto);

        when(parentGuardianService.getMinorsForGuardian(guardianId)).thenReturn(mockResponse);

        ResponseEntity<List<ParentGuardianDTO>> response = parentGuardianController.getMinorsForGuardian(guardianId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(parentGuardianService, times(1)).getMinorsForGuardian(guardianId);
    }
}
