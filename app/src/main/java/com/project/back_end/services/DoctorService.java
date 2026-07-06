package com.project.back_end.services;

import com.project.back_end.controllers.DoctorController;
import java.util.List;

/**
 * Service layer responsible for enforcing medical business rules, validation protocols,
 * and access workflows associated with Doctor user roles.
 */
public class DoctorService {

    private final DoctorController doctorController;
    private final TokenService tokenService;

    public DoctorService(DoctorController doctorController, TokenService tokenService) {
        this.doctorController = doctorController;
        this.tokenService = tokenService;
    }

    /**
     * Fulfills US-101: View Daily Schedule
     * Verifies that the accessing session token is authorized before pulling clinical schedules.
     */
    public List<String> getAuthorizedDailySchedule(String sessionToken, Long doctorId) {
        // Guardrail: Ensure session token is active and explicitly belongs to a DOCTOR role
        if (!tokenService.isTokenValidForRole(sessionToken, "DOCTOR")) {
            System.err.println("Access Denied: Invalid security session context.");
            return null;
        }
        return doctorController.getDailySchedule(doctorId);
    }

    /**
     * Fulfills US-103: Update Medical Logs & Diagnosis
     * Validates that clinical entries are not completely blank before writing down to MySQL.
     */
    public boolean submitClinicalConsultation(String sessionToken, Long patientId, Long appointmentId, String chiefComplaint, String diagnosis) {
        // Guardrail: Ensure the user submitting clinical records is an authenticated Doctor
        if (!tokenService.isTokenValidForRole(sessionToken, "DOCTOR")) {
            System.err.println("Access Denied: Only authenticated doctors can write clinical entries.");
            return false;
        }

        // Business Rule: Reject empty diagnosis logs
        if (chiefComplaint == null || chiefComplaint.trim().isEmpty() || 
            diagnosis == null || diagnosis.trim().isEmpty()) {
            System.err.println("Validation Error: Chief complaint and final medical diagnosis records cannot be blank.");
            return false;
        }

        return doctorController.addMedicalRecord(patientId, appointmentId, chiefComplaint.trim(), diagnosis.trim());
    }
}
