package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller layer for handling Doctor business operations and database queries.
 * Integrates directly with the MySQL schema to fulfill Doctor User Stories.
 */
public class DoctorController {

    private final Connection databaseConnection;

    // Constructor accepting a database connection object
    public DoctorController(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Fulfills US-101: View Daily Schedule
     * Fetches a clean list of scheduled times for a specific doctor from MySQL.
     */
    public List<String> getDailySchedule(Long doctorId) {
        List<String> scheduleList = new ArrayList<>();
        String query = "SELECT appointment_time, status FROM appointments " +
                       "WHERE doctor_id = ? AND status = 'SCHEDULED' " +
                       "ORDER BY appointment_time ASC";

        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(query)) {
            preparedStatement.setLong(1, doctorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp timestamp = resultSet.getTimestamp("appointment_time");
                    scheduleList.add(timestamp.toString());
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching daily schedule: " + e.getMessage());
        }
        return scheduleList;
    }

    /**
     * Fulfills US-103: Update Medical Logs & Diagnosis
     * Inserts new clinical summary entries directly into the medical_records table.
     */
    public boolean addMedicalRecord(Long patientId, Long appointmentId, String chiefComplaint, String diagnosis) {
        String insertSQL = "INSERT INTO medical_records (patient_id, appointment_id, chief_complaint, diagnosis) " +
                           "VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(insertSQL)) {
            preparedStatement.setLong(1, patientId);
            preparedStatement.setLong(2, appointmentId);
            preparedStatement.setString(3, chiefComplaint);
            preparedStatement.setString(4, diagnosis);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error saving medical log: " + e.getMessage());
            return false;
        }
    }
}
