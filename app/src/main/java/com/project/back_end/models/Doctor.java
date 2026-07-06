package com.project.back_end.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Doctor user within the Medical Clinic Management System.
 * Maps closely to the 'users' database schema with a specific DOCTOR role.
 */
public class Doctor {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String specialty;
    private List<String> dailySchedule; // Simulates booked appointment times

    // Default Constructor
    public Doctor() {
        this.dailySchedule = new ArrayList<>();
        this.isActive = true;
    }

    // Parameterized Constructor
    public Doctor(Long id, String email, String firstName, String lastName, String specialty) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.isActive = true;
        this.dailySchedule = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public List<String> getDailySchedule() {
        return dailySchedule;
    }

    public void addAppointmentToSchedule(String appointmentTime) {
        this.dailySchedule.add(appointmentTime);
    }

    /**
     * Business logic method supporting US-101 (View Daily Schedule).
     * Prints out a clean overview of the doctor's day.
     */
    public void displayDailySchedule() {
        System.out.println("=== Schedule for Dr. " + this.lastName + " ===");
        if (dailySchedule.isEmpty()) {
            System.out.println("No appointments scheduled for today.");
        } else {
            for (String time : dailySchedule) {
                System.out.println("- Scheduled Appointment at: " + time);
            }
        }
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='Dr. " + firstName + " " + lastName + '\'' +
                ", email='" + email + '\'' +
                ", specialty='" + specialty + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
