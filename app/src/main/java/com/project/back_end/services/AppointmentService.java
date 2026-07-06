package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Service layer responsible for coordinating business validation logic 
 * and safety guardrails surrounding appointment bookings and cancellations.
 */
public class AppointmentService {

    private final Connection databaseConnection;

    public AppointmentService(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Fulfills US-202: Self-Service Appointment Booking
     * Validates that a requested time slot is not already taken before executing the query.
     */
    public boolean bookAppointment(Long patientId, Long doctorId, LocalDateTime appointmentTime) {
        // Business Rule Guardrail: Cannot book appointments in the past
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            System.err.println("Validation Error: Cannot book an appointment in the past.");
            return false;
        }

        // Check if the doctor already has a conflicting appointment at that exact time
        String validationQuery = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_time = ? AND status = 'SCHEDULED'";
        String insertQuery = "INSERT INTO appointments (patient_id, doctor_id, appointment_time, status) VALUES (?, ?, ?, 'SCHEDULED')";

        try {
            // Check for scheduling conflict
            try (PreparedStatement checkStmt = databaseConnection.prepareStatement(validationQuery)) {
                checkStmt.setLong(1, doctorId);
                checkStmt.setTimestamp(2, Timestamp.valueOf(appointmentTime));
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.err.println("Validation Error: The selected time slot is already booked for this doctor.");
                        return false;
                    }
                }
            }

            // Execute booking transaction
            try (PreparedStatement insertStmt = databaseConnection.prepareStatement(insertQuery)) {
                insertStmt.setLong(1, patientId);
                insertStmt.setLong(2, doctorId);
                insertStmt.setTimestamp(3, Timestamp.valueOf(appointmentTime));
                
                int rowsAffected = insertStmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.err.println("Database error during appointment booking execution: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fulfills US-204: Reschedule or Cancel Appointments
     * Implements the business rule requiring a 24-hour notice before cancellation.
     */
    public boolean cancelAppointmentWithNotice(Long appointmentId) {
        String fetchQuery = "SELECT appointment_time, status FROM appointments WHERE id = ?";
        String updateQuery = "UPDATE appointments SET status = 'CANCELLED' WHERE id = ?";

        try {
            LocalDateTime appointmentTime = null;
            String status = "";

            try (PreparedStatement fetchStmt = databaseConnection.prepareStatement(fetchQuery)) {
                fetchStmt.setLong(1, appointmentId);
                try (ResultSet rs = fetchStmt.executeQuery()) {
                    if (rs.next()) {
                        appointmentTime = rs.getTimestamp("appointment_time").toLocalDateTime();
                        status = rs.getString("status");
                    } else {
                        System.err.println("Error: Appointment record not found.");
                        return false;
                    }
                }
            }

            // Business Rule Guardrail: Check for 24-hour buffer zone
            if (LocalDateTime.now().isAfter(appointmentTime.minusHours(24))) {
                System.err.println("Validation Error: Appointments can only be cancelled at least 24 hours in advance.");
                return false;
            }

            if ("COMPLETED".equals(status)) {
                System.err.println("Validation Error: Cannot cancel a completed appointment.");
                return false;
            }

            // Update status
            try (PreparedStatement updateStmt = databaseConnection.prepareStatement(updateQuery)) {
                updateStmt.setLong(1, appointmentId);
                int rowsAffected = updateStmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.err.println("Database error during cancellation execution: " + e.getMessage());
            return false;
        }
    }
}
