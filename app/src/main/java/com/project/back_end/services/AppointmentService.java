package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Injecting dependencies directly via constructor routing
    public AppointmentService(AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Fulfills Requirement: Book Appointment with direct persistence.
     * Maps and commits a new appointment entity directly into the system database.
     */
    @Transactional
    public Appointment bookAppointment(String sessionToken, Appointment appointment) {
        // Core Security Guardrail
        if (!tokenService.isTokenValidForRole(sessionToken, "PATIENT") && 
            !tokenService.isTokenValidForRole(sessionToken, "ADMIN")) {
            throw new SecurityException("Access Denied: Unauthorized session token context.");
        }

        // CRITICAL FIX: Directly interact with the repository tracking framework to save changes
        return appointmentRepository.save(appointment);
    }

    /**
     * Fulfills Requirement: Retrieve appointments filtered by doctor and a specific date target.
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctorAndDate(Doctor doctor, LocalDate date) {
        if (doctor == null || date == null) {
            throw new IllegalArgumentException("Validation Error: Doctor profile reference and calendar date cannot be null.");
        }

        // Establish the absolute boundary times for the target calendar date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Fetch using a custom query definition or filter via stream collection mappings
        return appointmentRepository.findByDoctorAndAppointmentTimeBetween(doctor, startOfDay, endOfDay);
    }

    /**
     * Cancels an existing booked appointment session slot out of the tracking record layer.
     */
    @Transactional
    public void cancelAppointment(String sessionToken, Long appointmentId) {
        if (!tokenService.isTokenValidForRole(sessionToken, "PATIENT") && 
            !tokenService.isTokenValidForRole(sessionToken, "ADMIN")) {
            throw new SecurityException("Access Denied: Unauthorized session token context.");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Error: Specified appointment entry ID does not exist."));
        
        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment); // Directly updates state flags back to database
    }
}
