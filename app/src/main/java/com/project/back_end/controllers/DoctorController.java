package com.project.back_end.controllers;

import com.project.back_end.services.TokenService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final TokenService tokenService;

    // Injecting TokenService via constructor to handle access validations
    public DoctorController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Exposes the required functional endpoint to retrieve a doctor's availability based on 
     * user role, doctor ID, date, and token context rules.
     * * URL Pattern Match: /api/doctors/availability/{user}/{doctorId}/{date}/{token}
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<List<String>> getDoctorAvailability(
            @PathVariable("user") String userRole,
            @PathVariable("doctorId") Long doctorId,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("token") String token) {

        // 1. Core Security Guardrail: Explicitly validate the token context against the expected user role
        // Supported role contexts mapped: "ADMIN", "DOCTOR", "PATIENT"
        if (!tokenService.isTokenValidForRole(token, userRole.toUpperCase())) {
            System.err.println("Unauthorized API Request: Provided token is invalid or lacks required permissions.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Business Logic Execution: Core lookup (Mocked database list execution response below)
        System.out.println("Fetching availability matrix for Doctor ID: " + doctorId + " on Date: " + date);
        List<String> mockAvailableSlots = new ArrayList<>();
        mockAvailableSlots.add("09:00 AM");
        mockAvailableSlots.add("11:30 AM");
        mockAvailableSlots.add("02:15 PM");
        mockAvailableSlots.add("04:00 PM");

        return ResponseEntity.ok(mockAvailableSlots);
    }

    /**
     * Legacy backup placeholder method for fallback compatibility layers.
     */
    public List<String> getDailySchedule(Long doctorId) {
        List<String> schedule = new ArrayList<>();
        schedule.add("09:00 AM - Patient Appointment #14");
        schedule.add("10:30 AM - Patient Appointment #18");
        return schedule;
    }

    /**
     * Legacy backup placeholder method for fallback consultation entries.
     */
    public boolean addMedicalRecord(Long patientId, Long appointmentId, String chiefComplaint, String diagnosis) {
        return true;
    }
}
