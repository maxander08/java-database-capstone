# Database Schema Design - Clinic Management System

This document details the relational MySQL database schema designed to support the Doctor, Patient, and Admin user stories.

## Entity Relationship Summary
* **Users & Roles:** A `users` table handles authentication, while a `roles` table assigns permissions (`ADMIN`, `DOCTOR`, `PATIENT`).
* **Appointments:** Links a `patient` and a `doctor` with a specific time slot.
* **Medical Records & Prescriptions:** Tracks clinical history and medications mapped to specific appointments and patients.

---

## Data Dictionary & Tables

### 1. `roles` Table
Stores system authorization groups.
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for the role. |
| `role_name` | VARCHAR(50) | UNIQUE, NOT NULL | e.g., 'ADMIN', 'DOCTOR', 'PATIENT'. |

### 2. `users` Table
Core credentials and identity mapping for all actors.
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for the user. |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL | Used for login credentials. |
| `password_hash` | VARCHAR(255) | NOT NULL | BCrypt hashed password storage. |
| `first_name` | VARCHAR(50) | NOT NULL | User's legal first name. |
| `last_name` | VARCHAR(50) | NOT NULL | User's legal last name. |
| `role_id` | INT | FOREIGN KEY references `roles(id)` | Ties user to their system access level. |
| `is_active` | BOOLEAN | DEFAULT TRUE | Allows admins to deactivate accounts. |

### 3. `appointments` Table
Manages the booking schedule between patients and doctors.
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique appointment ID. |
| `patient_id` | BIGINT | FOREIGN KEY references `users(id)` | The patient booking the visit. |
| `doctor_id` | BIGINT | FOREIGN KEY references `users(id)` | The assigned doctor. |
| `appointment_time` | DATETIME | NOT NULL | Scheduled date and time. |
| `status` | ENUM | NOT NULL | 'SCHEDULED', 'COMPLETED', 'CANCELLED'. |

### 4. `medical_records` Table
Stores historical clinical data logged by doctors.
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique record ID. |
| `patient_id` | BIGINT | FOREIGN KEY references `users(id)` | Associated patient. |
| `appointment_id` | BIGINT | FOREIGN KEY references `appointments(id)` | The specific visit this note belongs to. |
| `chief_complaint` | TEXT | NOT NULL | Symptoms described by the patient. |
| `diagnosis` | TEXT | NOT NULL | Doctor's final diagnosis. |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Unalterable timestamp of creation. |

### 5. `prescriptions` Table
Tracks medications issued during appointments.
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique prescription ID. |
| `medical_record_id` | BIGINT | FOREIGN KEY references `medical_records(id)` | Links prescription to the clinical visit. |
| `drug_name` | VARCHAR(150) | NOT NULL | Name of the medication. |
| `dosage` | VARCHAR(100) | NOT NULL | e.g., '500mg'. |
| `frequency` | VARCHAR(100) | NOT NULL | e.g., 'Twice daily'. |
| `duration_days` | INT | NOT NULL | Total days to take medication. |

---

## DDL SQL Generation Script
```sql
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_time DATETIME NOT NULL,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    FOREIGN KEY (patient_id) REFERENCES users(id),
    FOREIGN KEY (doctor_id) REFERENCES users(id)
);

CREATE TABLE medical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT NOT NULL,
    chief_complaint TEXT NOT NULL,
    diagnosis TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id BIGINT NOT NULL,
    drug_name VARCHAR(150) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration_days INT NOT NULL,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id)
);

---

## 2. Steps to Push and Generate Your Link

1. **Save the File:** Save the content above as `schema-design.md` inside your local repository folder.
2. **Push to GitHub:** Open your terminal and run the following commands to upload it to your remote repository:
   ```bash
   git add schema-design.md
   git commit -m "Add MySQL database schema design documentation"
   git push origin main
