package com.appointment.backend.controller;

import com.appointment.backend.model.Appointment;
import com.appointment.backend.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * REST controller for managing appointments.
 * Defines API endpoints for CRUD operations and for retrieving
 * previous/upcoming appointments.
 */
@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*") // Allows requests from any frontend origin (CORS)
public class AppointmentController {

    // Service that stores and manages appointments in memory
    private final AppointmentService appointmentService = new AppointmentService();

    /**
     * Retrieves all appointments (unsorted).
     */
    @GetMapping
    public Collection<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    /**
     * Retrieves a single appointment by its ID.
     */
    @GetMapping("/{id}")
    public Appointment getAppointment(@PathVariable String id) {
        return appointmentService.getAppointment(id);
    }

    /**
     * Adds a new appointment.
     * If validation fails, an exception will be thrown and handled globally.
     */
    @PostMapping
    public void addAppointment(@RequestBody Appointment appointment) {
        appointmentService.addAppointment(appointment);
    }

    /**
     * Deletes an appointment by ID.
     * Throws an exception if the ID does not exist.
     */
    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
    }

    /**
     * Retrieves upcoming appointments.
     * This includes appointments happening today or in the future, sorted soonest
     * to farthest.
     */
    @GetMapping("/upcoming")
    public List<Appointment> getUpcomingAppointments() {
        return appointmentService.getUpcomingAppointments();
    }

    /**
     * Retrieves previous appointments.
     * This includes appointments before today, sorted most recent to oldest.
     */
    @GetMapping("/previous")
    public List<Appointment> getPreviousAppointments() {
        return appointmentService.getPreviousAppointments();
    }
}
