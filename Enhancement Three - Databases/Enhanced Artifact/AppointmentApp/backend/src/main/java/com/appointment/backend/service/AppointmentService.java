package com.appointment.backend.service;

import com.appointment.backend.model.Appointment;
import com.appointment.backend.repo.AppointmentRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Original file from CS-320 Artifact, with enhancements made.
 * Business logic for appointments.
 * Handles validation, duplicate checking, and delegates persistence to the
 * repository.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository repo;

    public AppointmentService(AppointmentRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new appointment.
     * - Checks for duplicate IDs before saving.
     * - Handles both app-level and DB-level duplicate errors.
     */
    public void addAppointment(Appointment appointment) {
        // Friendly duplicate message + unique index safety net
        if (repo.existsById(appointment.getAppointmentId())) {
            throw new IllegalArgumentException("Appointment ID already exists");
        }
        try {
            repo.save(appointment);
        } catch (DuplicateKeyException ex) {
            throw new IllegalArgumentException("Appointment ID already exists");
        }
    }

    /**
     * Delete an appointment by ID.
     * Throws if the ID does not exist.
     */
    public void deleteAppointment(String appointmentId) {
        if (!repo.existsById(appointmentId)) {
            throw new IllegalArgumentException("Appointment ID does not exist");
        }
        repo.deleteById(appointmentId);
    }

    /** Find an appointment by ID (returns null if not found). */
    public Appointment getAppointment(String appointmentId) {
        return repo.findById(appointmentId).orElse(null);
    }

    /** Return all appointments (unsorted). */
    public Collection<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    /** Return all appointments sorted by date ascending. */
    public List<Appointment> getAllSortedByDate() {
        return repo.findAllByOrderByAppointmentDateAsc();
    }

    /** Return upcoming appointments (today or later). */
    public List<Appointment> getUpcomingAppointments() {
        LocalDate today = LocalDate.now();
        return repo.findAllByAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(today);
    }

    /** Return past appointments (before today). */
    public List<Appointment> getPreviousAppointments() {
        LocalDate today = LocalDate.now();
        return repo.findAllByAppointmentDateLessThanOrderByAppointmentDateDesc(today);
    }

    /**
     * Return appointments within a given date range.
     * Validates that both bounds are present and that end >= start.
     */
    public List<Appointment> getAppointmentsInRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
        return repo.findAllByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }
}
