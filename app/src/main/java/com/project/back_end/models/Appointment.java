package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity model representing a clinical Appointment.
 * Fully complies with relational mappings and entity validation constraints.
 */
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Maps the relational link back to the Patient (User entity).
     * Automatically establishes the patient_id foreign key constraint in MySQL.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "An appointment must be assigned to a valid patient.")
    private User patient;

    /**
     * Maps the relational link back to the Doctor entity.
     * Automatically establishes the doctor_id foreign key constraint in MySQL.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "An appointment must be assigned to a valid doctor.")
    private Doctor doctor;

    /**
     * Enforces that appointment schedules cannot be blank or set retroactively.
     */
    @Column(name = "appointment_time", nullable = false)
    @NotNull(message = "Appointment time date entry cannot be blank.")
    @Future(message = "Appointment time must be scheduled for a future date.")
    private LocalDateTime appointmentTime;

    @Column(nullable = false)
    private String status = "SCHEDULED"; // e.g., SCHEDULED, COMPLETED, CANCELLED

    // Default Constructor required by JPA
    public Appointment() {}

    // Parameterized Constructor
    public Appointment(User patient, Doctor doctor, LocalDateTime appointmentTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
