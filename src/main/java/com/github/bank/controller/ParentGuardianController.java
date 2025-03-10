package com.github.bank.controller;

import com.github.bank.model.DTO.ParentGuardianDTO;
import com.github.bank.service.ParentGuardianService;
import com.github.bank.util.Enum.RelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parent-guardian")
public class ParentGuardianController {

    private final ParentGuardianService parentGuardianService;

    @Autowired
    public ParentGuardianController(ParentGuardianService parentGuardianService) {
        this.parentGuardianService = parentGuardianService;
    }

    @PostMapping("/link")
    public ResponseEntity<String> linkGuardianToMinor(
            @RequestParam Long minorId,
            @RequestParam Long guardianId,
            @RequestParam RelationshipType relationshipType) {
        parentGuardianService.linkGuardianToMinor(minorId, guardianId, relationshipType);
        return new ResponseEntity<>("Guardian linked successfully.", HttpStatus.CREATED);
    }

    @GetMapping("/minor/{minorId}")
    public ResponseEntity<List<ParentGuardianDTO>> getGuardiansForMinor(@PathVariable Long minorId) {
        return new ResponseEntity<>(parentGuardianService.getGuardiansForMinor(minorId), HttpStatus.OK);
    }

    @GetMapping("/guardian/{guardianId}")
    public ResponseEntity<List<ParentGuardianDTO>> getMinorsForGuardian(@PathVariable Long guardianId) {
        return new ResponseEntity<>(parentGuardianService.getMinorsForGuardian(guardianId), HttpStatus.OK);
    }
}
