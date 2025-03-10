package com.github.bank.service;

import com.github.bank.exception.ResourceNotFoundException;
import com.github.bank.model.DTO.IndividualUserDTO;
import com.github.bank.model.Entity.IndividualUser;
import com.github.bank.repository.IndividualUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IndividualUserServiceTest {

    @Mock
    private IndividualUserRepository userRepository;

    @InjectMocks
    private IndividualUserService individualUserService;

    private IndividualUserDTO userDTO;
    private IndividualUser existingUser;

    @BeforeEach
    public void setUp() throws ParseException {
        userDTO = new IndividualUserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setNationalId("12345");
        userDTO.setPhoneNumber("9876543210");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = sdf.parse("2000-01-01");
        userDTO.setDateOfBirth(dob);
        userDTO.setAddress(null);

        existingUser = new IndividualUser();
        existingUser.setId(1L);
        existingUser.setFirstName("Jane");
        existingUser.setLastName("Doe");
        existingUser.setNationalId("54321");
        existingUser.setPhoneNumber("1234567890");
        dob = sdf.parse("2000-01-01");
        existingUser.setDateOfBirth(dob);
        existingUser.setDateOfBirth(dob);
    }

    @Test
    public void testCreateUser() {
        when(userRepository.save(any(IndividualUser.class))).thenReturn(new IndividualUser() {{
            setId(1L);
        }});

        Long newUserId = individualUserService.createUser(userDTO);

        verify(userRepository, times(1)).save(any(IndividualUser.class));
        assertEquals(1L, newUserId);
    }

    @Test
    public void testUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.save(any(IndividualUser.class))).thenReturn(existingUser);

        userDTO.setFirstName("John");
        userDTO.setLastName("Smith");

        individualUserService.updateUser(1L, userDTO);

        assertEquals("John", existingUser.getFirstName());
        assertEquals("Smith", existingUser.getLastName());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testUpdateUserWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            individualUserService.updateUser(1L, userDTO);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyDTO() {
        IndividualUserDTO emptyDTO = new IndividualUserDTO();

        when(userRepository.save(any(IndividualUser.class))).thenReturn(new IndividualUser() {{
            setId(2L);
        }});

        Long newUserId = individualUserService.createUser(emptyDTO);

        verify(userRepository, times(1)).save(any(IndividualUser.class));
        assertNotNull(newUserId);
    }
}