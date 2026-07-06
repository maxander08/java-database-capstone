# Medical Clinic Management System - User Stories

This document outlines the core user stories defining the requirements for the Doctor, Patient, and Admin roles within the application.

---

## 1. Doctor User Stories

### US-101: View Daily Schedule
* **As a** Doctor  
* **I want to** view a consolidated list of my daily scheduled appointments  
* **So that** I can manage my time efficiently and prepare for each patient before they arrive.  
* **Acceptance Criteria:**  
  * The dashboard displays appointments sorted chronologically by time.
  * Each entry shows the patient's name, age, appointment time, and visit type (e.g., Check-up, Follow-up).

### US-102: Access Patient Electronic Health Records (EHR)
* **As a** Doctor  
* **I want to** view a patient's medical history, allergies, and past prescriptions during their visit  
* **So that** I can make informed diagnoses and avoid harmful drug interactions.  
* **Acceptance Criteria:**  
  * A search bar allows looking up patients by Name or Patient ID.
  * Medical history logs are read-only to prevent unauthorized historical alterations.

### US-103: Update Medical Logs & Diagnosis
* **As a** Doctor  
* **I want to** log symptoms, diagnoses, and vitals into the patient's record during or immediately after a consultation  
* **So that** their medical record stays continuously up to date.  
* **Acceptance Criteria:**  
  * The system provides text fields for "Chief Complaint", "Diagnosis", and "Clinical Notes".
  * Changes are timestamped and digitally signed under the logged-in doctor's credentials upon submission.

### US-104: Issue Digital Prescriptions
* **As a** Doctor  
* **I want to** write and submit electronic prescriptions directly into the system  
* **So that** the patient can retrieve their medications seamlessly and the pharmacy gets accurate records.  
* **Acceptance Criteria:**  
  * Includes input validation for drug names, dosages, frequency, and duration.
  * System prompts a warning if a drug clashes with the patient's listed allergies.

---

## 2. Patient User Stories

### US-201: Account Registration & Portal Creation
* **As a** New Patient  
* **I want to** register for an account using my email and personal details  
* **So that** I can securely access the clinic's digital services.  
* **Acceptance Criteria:**  
  * Registration requires full name, date of birth, phone number, email, and a secure password.
  * Sends a verification email to activate the account.

### US-202: Self-Service Appointment Booking
* **As a** Registered Patient  
* **I want to** select a medical specialty, choose an available doctor, and pick an open time slot  
* **So that** I can schedule a visit at my own convenience without calling the clinic front desk.  
* **Acceptance Criteria:**  
  * Displays only real-time available time slots for selected doctors.
  * Generates an immediate booking confirmation email and in-app notification.

### US-203: View Personal Prescription History
* **As a** Patient  
* **I want to** log into my portal and view a history of all my current and past prescriptions  
* **So that** I can accurately follow my treatment plan and track dosage instructions.  
* **Acceptance Criteria:**  
  * Lists active medications prominently at the top.
  * Displays prescribing doctor, date issued, dosage instructions, and remaining refills.

### US-204: Reschedule or Cancel Appointments
* **As a** Patient  
* **I want to** modify or cancel my upcoming appointments directly from my dashboard  
* **So that** I can update my schedule when unexpected conflicts arise.  
* **Acceptance Criteria:**  
  * Cancellations/rescheduling must be done at least 24 hours prior to the slot.
  * Frees up the time slot immediately back into the public pool upon cancellation.

---

## 3. Admin User Stories

### US-301: Staff Identity & Access Management (IAM)
* **As an** Admin  
* **I want to** provision, update, and deactivate user accounts for doctors, nurses, and receptionist staff  
* **So that** system security is maintained and only authorized personnel have access to clinical data.  
* **Acceptance Criteria:**  
  * Admin can assign specific roles (e.g., `ROLE_DOCTOR`, `ROLE_ADMIN`, `ROLE_RECEPTIONIST`) which enforce strict Role-Based Access Control (RBAC).
  * Option to immediately revoke access if an employee leaves the organization.

### US-302: Doctor Roster & Shift Scheduling
* **As an** Admin  
* **I want to** input and modify the operational shifts, weekly clinic hours, and vacation days for all doctors  
* **So that** the patient-facing calendar accurately reflects when appointments can be booked.  
* **Acceptance Criteria:**  
  * Prevents booking time slots during a doctor's marked time-off or outside standard clinic hours.
  * Updates reflect across the user interface instantly.

### US-303: Database Backup & Maintenance Auditing
* **As an** Admin  
* **I want to** access system activity logs and trigger data backups  
* **So that** the system remains compliant with medical data privacy regulations and prevents data loss.  
* **Acceptance Criteria:**  
  * Logs capture who accessed what clinical record, along with a precise timestamp.
  * Backups can be scheduled to run automatically or generated manually via an admin console button.

### US-304: Clinic-Wide Analytics Reporting
* **As an** Admin  
* **I want to** generate monthly reports on total patient visits, appointment cancellation rates, and peak booking times  
* **So that** clinic management can optimize staffing levels and improve overall operational efficiency.  
* **Acceptance Criteria:**  
  * Provides a visual dashboard summarizing key metrics.
  * Allows exporting data cleanly into CSV or PDF formats.
