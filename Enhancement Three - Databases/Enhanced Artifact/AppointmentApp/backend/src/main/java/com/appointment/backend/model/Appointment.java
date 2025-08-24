package com.appointment.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * Original file from CS-320 Artifact, with enhancements made.
 * MongoDB document representing an appointment.
 * Basic invariants (lengths, date floor) are enforced in the
 * constructor/setter.
 */
@Document(collection = "appointments")
public class Appointment {

    @Id
    @Indexed(unique = true) // ensure appointmentId is unique across the collection
    private final String appointmentId;

    @JsonFormat(pattern = "yyyy-MM-dd") // serialize/deserialize dates in ISO yyyy-MM-dd
    @Indexed // index to speed up date-based queries (upcoming/previous/range)
    private final LocalDate appointmentDate;

    private String description;

    public Appointment(String appointmentId, LocalDate appointmentDate, String description) {
        // Guard clauses keep the object in a valid state at creation time
        if (appointmentId == null || appointmentId.trim().isEmpty() || appointmentId.length() > 10) {
            throw new IllegalArgumentException("Appointment ID must be 1–10 characters.");
        }
        if (appointmentDate == null) {
            throw new IllegalArgumentException("Appointment date is required (yyyy-MM-dd).");
        }
        if (appointmentDate.isBefore(LocalDate.of(2000, 1, 1))) {
            throw new IllegalArgumentException("Appointment date cannot be before 2000-01-01.");
        }
        if (description == null || description.trim().isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("Description must be 1–50 characters.");
        }

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

    /** Update description with the same validation rules as construction. */
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("Description must be 1–50 characters.");
        }
        this.description = description.trim();
    }
}
