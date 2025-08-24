package com.appointment.backend.service;

import com.appointment.backend.model.Appointment;

import java.time.LocalDate;
import java.util.*;

/**
 * In-memory data store with two complementary indexes:
 * - byId: HashMap for O(1) lookups/deletes by appointmentId
 * - byDate: TreeMap (sorted) for efficient date-based queries and ordering
 *
 * Keeping both in sync gives fast ID operations and fast sorted/range reads.
 */
public class AppointmentService {

    private final Map<String, Appointment> byId = new HashMap<>();
    private final NavigableMap<LocalDate, List<Appointment>> byDate = new TreeMap<>();

    /**
     * Create a new appointment.
     * - Rejects duplicate IDs.
     * - Adds to the appropriate date bucket.
     * Synchronized to avoid index drift if called concurrently.
     */
    public synchronized void addAppointment(Appointment appointment) {
        String id = appointment.getAppointmentId();
        if (byId.containsKey(id)) {
            throw new IllegalArgumentException("Appointment ID already exists");
        }
        byId.put(id, appointment);

        byDate.computeIfAbsent(appointment.getAppointmentDate(), d -> new ArrayList<>())
                .add(appointment);
    }

    /**
     * Delete an appointment by ID.
     * - Removes from byId
     * - Also removes from the associated date bucket (and drops the empty bucket)
     */
    public synchronized void deleteAppointment(String appointmentId) {
        Appointment removed = byId.remove(appointmentId);
        if (removed == null) {
            throw new IllegalArgumentException("Appointment ID does not exist");
        }

        LocalDate date = removed.getAppointmentDate();
        List<Appointment> bucket = byDate.get(date);
        if (bucket != null) {
            bucket.removeIf(a -> a.getAppointmentId().equals(appointmentId));
            if (bucket.isEmpty()) {
                byDate.remove(date);
            }
        }
    }

    /** Lookup by ID (null if not found). */
    public Appointment getAppointment(String appointmentId) {
        return byId.get(appointmentId);
    }

    /**
     * Unsorted collection of all appointments.
     * Use getAllSortedByDate() if you need chronological order.
     */
    public Collection<Appointment> getAllAppointments() {
        return byId.values();
    }

    /** All appointments in ascending date order. */
    public List<Appointment> getAllSortedByDate() {
        List<Appointment> out = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Appointment>> e : byDate.entrySet()) {
            out.addAll(e.getValue());
        }
        return out;
    }

    /** Today and future, ascending by date. */
    public List<Appointment> getUpcomingAppointments() {
        LocalDate today = LocalDate.now();
        List<Appointment> out = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Appointment>> e : byDate.tailMap(today, true).entrySet()) {
            out.addAll(e.getValue());
        }
        return out;
    }

    /** Strictly before today, returned newest → oldest. */
    public List<Appointment> getPreviousAppointments() {
        LocalDate today = LocalDate.now();
        List<Appointment> out = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Appointment>> e : byDate.headMap(today, false).descendingMap().entrySet()) {
            out.addAll(e.getValue());
        }
        return out;
    }

    /**
     * Inclusive date range [start, end], ascending by date.
     * Validates presence and ordering of the bounds.
     */
    public List<Appointment> getAppointmentsInRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }

        List<Appointment> out = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Appointment>> e : byDate.subMap(start, true, end, true).entrySet()) {
            out.addAll(e.getValue());
        }
        return out;
    }
}
