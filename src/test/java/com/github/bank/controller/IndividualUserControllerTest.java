package com.github.bank.controller;

import com.github.bank.service.IndividualUserService;
import com.github.bank.model.DTO.IndividualUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndividualUserControllerTest {

    @Mock
    private IndividualUserService individualUserService;

    @InjectMocks
    private IndividualUserController individualUserController;

    @BeforeEach
    void setUp() {
        reset(individualUserService);
    }

    @Test
    void testCreateUser() {
        IndividualUserDTO userDTO = new IndividualUserDTO();
        when(individualUserService.createUser(userDTO)).thenReturn(1L);

        ResponseEntity<String> response = individualUserController.createUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User 1 created successfully!", response.getBody());
        verify(individualUserService, times(1)).createUser(userDTO);
    }

    @Test
    void testUpdateUser() {
        Long userId = 2L;
        IndividualUserDTO userDTO = new IndividualUserDTO();

        ResponseEntity<String> response = individualUserController.updateUser(userId, userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account 2 updated successfully!", response.getBody());
        verify(individualUserService, times(1)).updateUser(userId, userDTO);
    }
}
