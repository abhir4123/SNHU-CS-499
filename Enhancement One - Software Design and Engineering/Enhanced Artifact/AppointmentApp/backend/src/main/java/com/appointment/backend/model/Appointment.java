package com.appointment.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Model class representing an Appointment.
 * Contains validation logic to ensure only valid data is stored.
 */
public class Appointment {

    // Unique appointment ID (1–10 characters)
    private final String appointmentId;

    // Appointment date in yyyy-MM-dd format
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate appointmentDate;

    // Description of the appointment (1–50 characters)
    private String description;

    /**
     * Constructor that validates and creates an Appointment object.
     */
    public Appointment(String appointmentId, LocalDate appointmentDate, String description) {
        // Validate ID
        if (appointmentId == null || appointmentId.trim().isEmpty() || appointmentId.length() > 10) {
            throw new IllegalArgumentException("Appointment ID must be 1–10 characters.");
        }

        // Validate date
        if (appointmentDate == null) {
            throw new IllegalArgumentException("Appointment date is required (yyyy-MM-dd).");
        }
        LocalDate earliestAllowedDate = LocalDate.of(2000, 1, 1);
        if (appointmentDate.isBefore(earliestAllowedDate)) {
            throw new IllegalArgumentException("Appointment date cannot be before 2000-01-01.");
        }

        // Validate description
        if (description == null || description.trim().isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("Description must be 1–50 characters.");
        }

        // Assign values after trimming whitespace
        this.appointmentId = appointmentId.trim();
        this.appointmentDate = appointmentDate;
        this.description = description.trim();
    }

    // Getters
    public String getAppointmentId() {
        return appointmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public String getDescription() {
        return description;
    }

    // Setter for description (with validation)
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("Description must be 1–50 characters.");
        }
        this.description = description.trim();
    }
}
