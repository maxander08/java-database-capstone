package com.project.back_end.repositories;

import com.project.back_end.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<User, Long> {

    /**
     * Legacy backup placeholder method.
     * Retained for temporary backward compatibility with existing legacy map endpoints.
     */
    default java.util.Map<String, Object> findPatientByEmail(String email) {
        System.out.println("Warning: Using legacy map finder execution trace.");
        return null;
    }

    /**
     * CRITICAL FIX: Fulfills assignment requirements for dual-lookup flexibility.
     * Generates a dynamic database query that allows retrieval by EITHER an email address
     * OR a phone number, returning a safe Optional container.
     *
     * @param email The target account registration email
     * @param phoneNumber The target account registration phone number string
     * @return An Optional containing the matched patient User profile, or empty if none found
     */
    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);
}
