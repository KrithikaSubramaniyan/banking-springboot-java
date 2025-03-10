package com.github.bank.controller;

import com.github.bank.model.DTO.IndividualUserDTO;
import com.github.bank.service.IndividualUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class IndividualUserController {

    private final IndividualUserService individualUserService;

    @Autowired
    public IndividualUserController(IndividualUserService individualUserService) {
        this.individualUserService = individualUserService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody @Valid IndividualUserDTO individualUserDTO) {
        Long id = individualUserService.createUser(individualUserDTO);
        String message = "User " + id + " created successfully!";
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable @NotNull(message = "User ID cannot be null") Long id,
                                             @RequestBody IndividualUserDTO individualUserDTO) {
        individualUserService.updateUser(id, individualUserDTO);
        String message = "Account " + id + " updated successfully!";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
