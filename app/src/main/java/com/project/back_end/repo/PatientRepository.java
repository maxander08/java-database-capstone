package com.project.back_end.repo;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object (DAO) / Repository layer handling low-level SQL execution 
 * on the 'users' table specifically for Patient accounts.
 */
public class PatientRepository {

    private final Connection databaseConnection;

    public PatientRepository(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Fulfills US-201: Account Registration & Portal Creation
     * Inserts a new patient record directly into the MySQL database mapping to the PATIENT role.
     */
    public boolean registerPatient(String email, String passwordHash, String firstName, String lastName) {
        // Enforces role mapping constraint based on DDL configuration
        String insertSQL = "INSERT INTO users (email, password_hash, first_name, last_name, role_id, is_active) " +
                           "VALUES (?, ?, ?, ?, (SELECT id FROM roles WHERE role_name = 'PATIENT'), TRUE)";

        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, passwordHash);
            preparedStatement.setString(3, firstName);
            preparedStatement.setString(4, lastName);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error during patient account creation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to verify and retrieve core Patient profiles by Email profile indices.
     */
    public Map<String, Object> findPatientByEmail(String email) {
        Map<String, Object> patientProfile = null;
        String selectSQL = "SELECT u.id, u.email, u.first_name, u.last_name, u.is_active " +
                           "FROM users u " +
                           "JOIN roles r ON u.role_id = r.id " +
                           "WHERE u.email = ? AND r.role_name = 'PATIENT'";

        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    patientProfile = new HashMap<>();
                    patientProfile.put("id", resultSet.getLong("id"));
                    patientProfile.put("email", resultSet.getString("email"));
                    patientProfile.put("firstName", resultSet.getString("first_name"));
                    patientProfile.put("lastName", resultSet.getString("last_name"));
                    patientProfile.put("isActive", resultSet.getBoolean("is_active"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error reading patient profile data: " + e.getMessage());
        }
        return patientProfile;
    }
}
