package com.project.back_end.controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller layer responsible for routing prescription transactions 
 * between the MySQL database and the user interface application layer.
 */
public class PrescriptionController {

    private final Connection databaseConnection;

    public PrescriptionController(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Fulfills US-104: Issue Digital Prescriptions
     * Inserts a brand new medication record mapped directly to a specific medical visit.
     */
    public boolean issuePrescription(Long medicalRecordId, String drugName, String dosage, String frequency, int durationDays) {
        String insertSQL = "INSERT INTO prescriptions (medical_record_id, drug_name, dosage, frequency, duration_days) " +
                           "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(insertSQL)) {
            preparedStatement.setLong(1, medicalRecordId);
            preparedStatement.setString(2, drugName);
            preparedStatement.setString(3, dosage);
            preparedStatement.setString(4, frequency);
            preparedStatement.setInt(5, durationDays);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error issuing digital prescription: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fulfills US-203: View Personal Prescription History
     * Performs a relational JOIN lookup to pull all medications belonging to a specific patient ID.
     */
    public List<Map<String, Object>> getPatientPrescriptionHistory(Long patientId) {
        List<Map<String, Object>> historyList = new ArrayList<>();
        String joinQuery = "SELECT p.id, p.drug_name, p.dosage, p.frequency, p.duration_days, mr.created_at " +
                          "FROM prescriptions p " +
                          "JOIN medical_records mr ON p.medical_record_id = mr.id " +
                          "WHERE mr.patient_id = ? " +
                          "ORDER BY mr.created_at DESC";

        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(joinQuery)) {
            preparedStatement.setLong(1, patientId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("prescriptionId", resultSet.getLong("id"));
                    row.put("drugName", resultSet.getString("drug_name"));
                    row.put("dosage", resultSet.getString("dosage"));
                    row.put("frequency", resultSet.getString("frequency"));
                    row.put("durationDays", resultSet.getInt("duration_days"));
                    row.put("dateIssued", resultSet.getTimestamp("created_at").toString());
                    
                    historyList.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error pulling prescription logs: " + e.getMessage());
        }
        return historyList;
    }
}
