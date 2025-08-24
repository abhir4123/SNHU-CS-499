package com.appointment.backend.service;

import com.appointment.backend.model.Appointment;
import java.time.LocalDate;
import java.util.*;

/**
 * Service layer that manages appointment storage and retrieval.
 * Stores appointments in memory using a HashMap (no database).
 */
public class AppointmentService {

    // In-memory appointment storage (key = appointment ID)
    private final Map<String, Appointment> appointments = new HashMap<>();

    /**
     * Adds a new appointment to storage.
     * Throws an exception if the ID already exists.
     */
    public void addAppointment(Appointment appointment) {
        if (appointments.containsKey(appointment.getAppointmentId())) {
            throw new IllegalArgumentException("Appointment ID already exists");
        }
        appointments.put(appointment.getAppointmentId(), appointment);
    }

    /**
     * Deletes an appointment by ID.
     * Throws an exception if the ID does not exist.
     */
    public void deleteAppointment(String appointmentId) {
        if (!appointments.containsKey(appointmentId)) {
            throw new IllegalArgumentException("Appointment ID does not exist");
        }
        appointments.remove(appointmentId);
    }

    /**
     * Retrieves a single appointment by its ID.
     * Returns null if it does not exist.
     */
    public Appointment getAppointment(String appointmentId) {
        return appointments.get(appointmentId);
    }

    /**
     * Retrieves all appointments without sorting.
     */
    public Collection<Appointment> getAllAppointments() {
        return appointments.values();
    }

    /**
     * Retrieves upcoming appointments.
     * Includes appointments today or in the future, sorted soonest to farthest.
     */
    public List<Appointment> getUpcomingAppointments() {
        LocalDate today = LocalDate.now();
        List<Appointment> upcoming = new ArrayList<>();

        for (Appointment a : appointments.values()) {
            if (!a.getAppointmentDate().isBefore(today)) { // >= today
                upcoming.add(a);
            }
        }

        upcoming.sort(Comparator.comparing(Appointment::getAppointmentDate));
        return upcoming;
    }

    /**
     * Retrieves previous appointments.
     * Includes appointments before today, sorted most recent to oldest.
     */
    public List<Appointment> getPreviousAppointments() {
        LocalDate today = LocalDate.now();
        List<Appointment> previous = new ArrayList<>();

        for (Appointment a : appointments.values()) {
            if (a.getAppointmentDate().isBefore(today)) {
                previous.add(a);
            }
        }

        previous.sort(Comparator.comparing(Appointment::getAppointmentDate).reversed());
        return previous;
    }
}
