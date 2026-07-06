package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.User;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    // Injecting dependencies through constructor routing
    public DoctorService(DoctorRepository doctorRepository, 
                         AppointmentRepository appointmentRepository, 
                         UserRepository userRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    /**
     * CRITICAL FIX: Explicit login validation method for Doctor credentials.
     * Verifies existence, password matches, and ensures the target account owns the DOCTOR role.
     *
     * @param email The login credential email
     * @param plainPassword The input raw text login password
     * @return A secure session token if validation succeeds
     * @throws SecurityException if credentials or role validations fail
     */
    @Transactional(readOnly = true)
    public String validateDoctorLogin(String email, String plainPassword) {
        // Look up user from identity schema tracking
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(plainPassword)) {
            throw new SecurityException("Authentication Failed: Invalid email address or password configuration.");
        }

        User user = userOpt.get();
        
        // Ensure user belongs to the target security clearance role
        if (!"DOCTOR".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new SecurityException("Access Denied: Authenticated account does not possess Doctor clearance privileges.");
        }

        // Return a fresh cryptographically signed session token upon valid authentication matching
        return tokenService.generateSessionToken(user.getEmail());
    }

    /**
     * CRITICAL FIX: Retrieves a doctor's actual available remaining time slots for a specific target date.
     * Compares the doctor's base operating shift slots against active appointments already registered in the DB.
     *
     * @param doctorId The unique identifier tracking target doctor
     * @param date The selected calendar evaluation target date
     * @return List of available time strings (e.g., ["09:00", "14:30"])
     */
    @Transactional(readOnly = true)
    public List<LocalTime> getDoctorAvailableTimeSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Lookup Error: Doctor identifier not found."));

        // 1. Establish standard base baseline shift operational hour markers (e.g., 9 AM to 5 PM)
        List<LocalTime> baseShiftSlots = new ArrayList<>();
        LocalTime slotTime = LocalTime.of(9, 0);
        while (slotTime.isBefore(LocalTime.of(17, 0))) {
            baseShiftSlots.add(slotTime);
            slotTime = slotTime.plusMinutes(30); // 30-minute block increments
        }

        // 2. Query all existing active appointments booked for this specific doctor on the target calendar day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorAndAppointmentTimeBetween(doctor, startOfDay, endOfDay);

        // Extract times that are already occupied
        List<LocalTime> occupiedTimes = bookedAppointments.stream()
                .filter(app -> !"CANCELLED".equalsIgnoreCase(app.getStatus()))
                .map(app -> app.getAppointmentTime().toLocalTime())
                .collect(Collectors.toList());

        // 3. Filter out occupied records from our operating shifts list to return pure open availabilities
        return baseShiftSlots.stream()
                .filter(time -> !occupiedTimes.contains(time))
                .collect(Collectors.toList());
    }

    /**
     * Legacy method placeholder tracking daily log schedule outputs.
     */
    public List<String> getDailySchedule(Long doctorId) {
        List<String> schedule = new ArrayList<>();
        schedule.add("09:00 AM - Patient Appointment #14");
        return schedule;
    }

    /**
     * Legacy backup placeholder method for fallback consultation entries.
     */
    public boolean addMedicalRecord(Long patientId, Long appointmentId, String chiefComplaint, String diagnosis) {
        return true;
    }
}
