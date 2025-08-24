package com.appointment.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Domain model for an appointment.
 * Holds a small amount of validation to keep bad data out of the system early.
 */
public class Appointment {

    // Unique identifier, 1–10 characters
    private final String appointmentId;

    // Date serialized/deserialized as yyyy-MM-dd
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate appointmentDate;

    // Short free-text description, 1–50 characters
    private String description;

    /**
     * Constructs an Appointment and enforces basic invariants.
     * Throws IllegalArgumentException with a human-readable message if invalid.
     */
    public Appointment(String appointmentId, LocalDate appointmentDate, String description) {
        // ID checks
        if (appointmentId == null || appointmentId.trim().isEmpty() || appointmentId.length() > 10) {
            throw new IllegalArgumentException("Appointment ID must be 1–10 characters.");
        }

        // Date checks
        if (appointmentDate == null) {
            throw new IllegalArgumentException("Appointment date is required (yyyy-MM-dd).");
        }
        if (appointmentDate.isBefore(LocalDate.of(2000, 1, 1))) {
            throw new IllegalArgumentException("Appointment date cannot be before 2000-01-01.");
        }

        // Description checks
        if (description == null || description.trim().isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("Description must be 1–50 characters.");
        }

        // Assign trimmed values
        this.appointmentId = appointmentId.trim();
        this.appointmentDate = appointmentDate;
        this.description = description.trim();
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Update description with the same validation rules used at construction.
     */
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("Description must be 1–50 characters.");
        }
        this.description = description.trim();
    }
}
