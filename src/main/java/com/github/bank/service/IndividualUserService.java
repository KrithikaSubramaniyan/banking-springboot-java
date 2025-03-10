package com.github.bank.service;

import com.github.bank.model.DTO.IndividualUserDTO;
import com.github.bank.model.Entity.IndividualUser;
import com.github.bank.repository.IndividualUserRepository;
import com.github.bank.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IndividualUserService {
    private final IndividualUserRepository userRepository;

    @Autowired
    public IndividualUserService(IndividualUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Long createUser(IndividualUserDTO userDTO) {
        IndividualUser newUser = new IndividualUser();

        newUser.setAddress(userDTO.getAddress());
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setDateOfBirth(userDTO.getDateOfBirth());
        newUser.setNationalId(userDTO.getNationalId());
        newUser.setPhoneNumber(userDTO.getPhoneNumber());
        Long newUserId = userRepository.save(newUser).getId();
        log.info("User created " + newUserId);
        return newUserId;
    }

    @Transactional
    public void updateUser(Long id, IndividualUserDTO userDTO) {

        IndividualUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        if (userDTO.getFirstName() != null && !userDTO.getFirstName().isEmpty()) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null && !userDTO.getLastName().isEmpty()) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getNationalId() != null && !userDTO.getNationalId().isEmpty()) {
            user.setNationalId(userDTO.getNationalId());
        }
        if (userDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userDTO.getDateOfBirth());
        }
        if (userDTO.getAddress() != null) {
            if (userDTO.getAddress().getHouseNumber() != null && !userDTO.getAddress().getHouseNumber().isEmpty()) {
                user.getAddress().setHouseNumber(userDTO.getAddress().getHouseNumber());
            }
            if (userDTO.getAddress().getStreet() != null && !userDTO.getAddress().getStreet().isEmpty()) {
                user.getAddress().setStreet(userDTO.getAddress().getStreet());
            }
            if (userDTO.getAddress().getTown() != null && !userDTO.getAddress().getTown().isEmpty()) {
                user.getAddress().setTown(userDTO.getAddress().getTown());
            }
            if (userDTO.getAddress().getState() != null && !userDTO.getAddress().getState().isEmpty()) {
                user.getAddress().setState(userDTO.getAddress().getState());
            }
            if (userDTO.getAddress().getPostCode() != null && !userDTO.getAddress().getPostCode().isEmpty()) {
                user.getAddress().setPostCode(userDTO.getAddress().getPostCode());
            }
            if (userDTO.getAddress().getCountry() != null && !userDTO.getAddress().getCountry().isEmpty()) {
                user.getAddress().setCountry(userDTO.getAddress().getCountry());
            }
        }
        userRepository.save(user);
    }
}
