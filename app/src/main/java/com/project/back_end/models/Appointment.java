package com.project.back_end.models;

import java.time.LocalDateTime;

/**
 * Represents an Appointment within the Medical Clinic Management System.
 * Maps closely to the 'appointments' database schema, linking a Patient and a Doctor.
 */
public class Appointment {

    // Enum to represent the state of the appointment
    public enum AppointmentStatus {
        SCHEDULED,
        COMPLETED,
        CANCELLED
    }

    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;

    // Default Constructor
    public Appointment() {
        this.status = AppointmentStatus.SCHEDULED;
    }

    // Parameterized Constructor
    public Appointment(Long id, Long patientId, Long doctorId, LocalDateTime appointmentTime) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentTime = appointmentTime;
        this.status = AppointmentStatus.SCHEDULED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    /**
     * Business logic method supporting US-204 (Reschedule or Cancel Appointments).
     * Ensures appointments can only be modified if they aren't already completed.
     */
    public boolean cancelAppointment() {
        if (this.status == AppointmentStatus.COMPLETED) {
            System.out.println("Error: Cannot cancel a completed appointment.");
            return false;
        }
        this.status = AppointmentStatus.CANCELLED;
        return true;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", appointmentTime=" + appointmentTime +
                ", status=" + status +
                '}';
    }
}
