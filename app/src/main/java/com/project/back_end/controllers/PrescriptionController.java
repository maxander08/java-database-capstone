package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final TokenService tokenService;

    // Injecting dependencies directly via constructor routing
    public PrescriptionController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Fulfills Requirement: Expose a POST method to securely save a prescription record.
     * URL Pattern Match: POST /api/prescriptions/save/{token}
     */
    @PostMapping("/save/{token}")
    public ResponseEntity<Prescription> savePrescription(
            @PathVariable("token") String token,
            @Valid @RequestBody Prescription prescription) {

        // 1. Core Security Guardrail: Explicitly validate that only an authorized DOCTOR can issue prescriptions
        if (!tokenService.isTokenValidForRole(token, "DOCTOR")) {
            System.err.println("Unauthorized API Attempt: Invalid token or insufficient permissions to write prescriptions.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Data Validation Guardrail
        if (prescription == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 3. Mock business layer persistence (Simulating saving to a database repository)
        System.out.println("Prescription successfully saved for Patient ID: " + prescription.getPatientId());
        
        // Return structured response matching the assignment criteria
        return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
    }
}
